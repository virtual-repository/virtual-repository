package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.ServiceProxy;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class VirtualRepoTest {

	Type<Asset> type = aType();
	Type<Asset> type2 = aType();

	@BeforeClass
	public static void setup() {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
	}

	@Test
	public void assetsCanBeDiscovered() throws Exception {

		ServiceProxy proxy1 = aProxy().with(anImporterFor(type)).get();
		ServiceProxy proxy2 = aProxy().with(anImporterFor(type)).get();

		RepositoryService service1 = aService().with(proxy1).get();
		RepositoryService service2 = aService().with(proxy2).get();

		Asset a1 = anAsset().of(type).in(service1);
		Asset a2 = anAsset().of(type).in(service2);
		Asset a3 = anAsset().of(type2).in(service2);

		when(proxy1.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1));
		when(proxy2.browser().discover(asList(type))).thenReturn((Iterable) singleton(a2));

		// test

		VirtualRepository repo = new Repository(service1, service2);

		int discovered = repo.discover(type);

		assertEquals(2, discovered);

		Asset retrieved = repo.lookup(a1.id());

		assertEquals(a1, retrieved);

		retrieved = repo.lookup(a2.id());

		assertEquals(a2, retrieved);

		try {
			repo.lookup(a3.id());
			fail();
		} catch (IllegalStateException e) {
		}

		assertEquals(2, asList(repo).size());
	}

	@Test
	public void assetsCanBeDiscoveredIncrementally() throws Exception {

		ServiceProxy proxy = aProxy().with(anImporterFor(type)).get();
		RepositoryService service1 = aService().with(proxy).get();

		Asset a1 = anAsset().of(type).in(service1);
		Asset a2 = anAsset().of(type).in(service1);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1), (Iterable) asList(a1, a2));

		// test

		VirtualRepository repo = new Repository(service1);

		int size = repo.discover(type);

		assertEquals(1, size);

		size = repo.discover(type);

		assertEquals(1, size);
	}

	@Test
	public void discoveryFailuresAreTolerated() throws Exception {

		ServiceProxy proxy = aProxy().with(anImporterFor(type)).get();
		RepositoryService service = aService().with(proxy).get();
		RepositoryService failing = aService().get();

		Asset a = anAsset().of(type).in(service);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a));
		when(failing.proxy().browser().discover(anyList())).thenThrow(new Exception());

		// test

		VirtualRepository repo = new Repository(service, failing);

		int discovered = repo.discover(type);

		assertEquals(1, discovered);
	}

	@Test
	public void retrievalFailsWithoutReader() {

		RepositoryService service = aService().get();

		Asset asset = anAsset().in(service);

		VirtualRepository virtual = new Repository(service);

		// no reader for integers
		try {
			virtual.retrieve(asset, Integer.class);
			fail();
		} catch (IllegalStateException e) {
		}

	}

	@Test
	public void assetsAreRetrieved() throws Exception {

		final int data = 10;

		Importer<Asset, Integer> importer = anImporterFor(type, Integer.class);

		ServiceProxy proxy = aProxy().with(importer).get();
		RepositoryService service = aService().with(proxy).get();

		Asset asset = anAsset().of(type).in(service);

		when(importer.retrieve(asset)).thenReturn(data);

		// test

		VirtualRepository virtual = new Repository(service);

		int imported = virtual.retrieve(asset, Integer.class);

		assertEquals(data, imported);
	}

	@Test
	public void assetsCanBePublished() throws Exception {

		Type<Asset> type = aType();
		Publisher<Asset, String> publisher = aPublisherFor(type, String.class);

		ServiceProxy proxy = aProxy().with(publisher).get();
		RepositoryService service = aService().with(proxy).get();

		Asset asset = anAsset().of(type).in(service);

		VirtualRepository virtual = new Repository(service);

		virtual.publish(asset, "hello");

		verify(publisher).publish(asset, "hello");

	}

	@Test
	@SuppressWarnings("unused")
	public void discoveryCanProceedInParallel() throws Exception {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
		ServiceProxy proxy = aProxy().with(anImporterFor(type)).get();
		final RepositoryService repo = aService().with(proxy).get();

		final List<Asset> assets = new ArrayList<Asset>();
		//prefill with many so iterating will take a bit
		for (int i = 0; i < 100000; i++)
			assets.add(new CsvCodelist(UUID.randomUUID().toString(),"name",0));
		
		//System.out.println("filled");

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) assets);

		final VirtualRepository virtual = new Repository(repo);

		final int load = 50;

		ExecutorService service = Executors.newFixedThreadPool(load);
		final CountDownLatch latch = new CountDownLatch(load);

		class Failure {
			int count;
			
		}
		
		final Failure failure = new Failure();
		
		for (int i = 0; i < load; i++) {
			final int frozen = i;
			service.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
						//write
						//writing threads work on same test data structure so we sync them
						//nothing to do with what we're testing: sync between writes and reads
						synchronized (assets) {
							//System.out.println("refreshing for "+frozen);
							///add one to spend most time iterating from other threads
							for (int i = 0; i < 50; i++) //new elements added, it's a structural modification
								assets.add(new CsvCodelist(UUID.randomUUID().toString(),"name",0));
							
							virtual.discover(type);
							//System.out.println("refreshed for "+frozen);
						}
						
						//read
						//System.out.println("visiting for "+frozen);
						int count = 0;
						for (Asset a : virtual)
							count++;
						//System.out.println("visited "+count+" for "+frozen);
					}
					catch(Exception e) {
						e.printStackTrace();
						failure.count++;
					}
					finally {
						latch.countDown();
						long count = latch.getCount();
						//System.out.println(frozen+" is done, "+count+" to go");
					}
				}
			});
		}


		latch.await();
		
		assertEquals(0,failure.count);
		
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
	}

}
