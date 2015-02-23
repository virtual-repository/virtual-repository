package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VirtualRepository.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class VirtualRepoTest {

	AssetType type = aType();
	AssetType type2 = aType();

	@BeforeClass
	public static void setup() {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
	}

	@Test
	public void assetsCanBeDiscovered() throws Exception {

		VirtualProxy proxy1 = aProxy().with(anImporterFor(type)).get();
		VirtualProxy proxy2 = aProxy().with(anImporterFor(type)).get();

		Repository service1 = aService().with(proxy1).get();
		Repository service2 = aService().with(proxy2).get();

		Asset a1 = anAsset().of(type).in(service1);
		Asset a2 = anAsset().of(type).in(service2);
		Asset a3 = anAsset().of(type2).in(service2);

		when(proxy1.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1));
		when(proxy2.browser().discover(asList(type))).thenReturn((Iterable) singleton(a2));

		// test

		VirtualRepository repo = repository(service1, service2);

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

		VirtualProxy proxy = aProxy().with(anImporterFor(type)).get();
		Repository service1 = aService().with(proxy).get();

		Asset a1 = anAsset().of(type).in(service1);
		Asset a2 = anAsset().of(type).in(service1);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1), (Iterable) asList(a1, a2));

		// test

		VirtualRepository repo = repository(service1);

		int size = repo.discover(type);

		assertEquals(1, size);

		size = repo.discover(type);

		assertEquals(1, size);
	}

	@Test
	public void discoveryFailuresAreTolerated() throws Exception {

		VirtualProxy proxy = aProxy().with(anImporterFor(type)).get();
		Repository service = aService().with(proxy).get();
		Repository failing = aService().get();

		Asset a = anAsset().of(type).in(service);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a));
		when(failing.proxy().browser().discover(anyList())).thenThrow(new Exception());

		// test

		VirtualRepository repo = repository(service, failing);

		int discovered = repo.discover(type);

		assertEquals(1, discovered);
	}

	@Test
	public void retrievalFailsWithoutReader() {

		Repository service = aService().get();

		Asset asset = anAsset().in(service);

		VirtualRepository virtual = repository(service);

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

		VirtualReader<Asset, Integer> importer = anImporterFor(type, Integer.class);

		VirtualProxy proxy = aProxy().with(importer).get();
		Repository service = aService().with(proxy).get();

		Asset asset = anAsset().of(type).in(service);

		when(importer.retrieve(asset)).thenReturn(data);

		// test

		VirtualRepository virtual = repository(service);

		int imported = virtual.retrieve(asset, Integer.class);

		assertEquals(data, imported);
	}

	@Test
	public void assetsCanBePublished() throws Exception {

		AssetType type = aType();
		VirtualWriter<Asset, String> publisher = aPublisherFor(type, String.class);

		VirtualProxy proxy = aProxy().with(publisher).get();
		Repository service = aService().with(proxy).get();

		Asset asset = anAsset().of(type).in(service);

		VirtualRepository virtual = repository(service);

		virtual.publish(asset, "hello");

		verify(publisher).publish(asset, "hello");

	}
	
	static class TestAsset extends Asset.Private {
		
		static AssetType type = ()->"test";
		
		protected TestAsset(String id, String name) {
			super(type, id, name);
		}

	}
	

	@Test
	@SuppressWarnings("unused")
	public void discoveryCanProceedInParallel() throws Exception {

		VirtualProxy proxy = aProxy().with(anImporterFor(type)).get();
		
		Answer<Iterable<Asset>> newAssets = new Answer<Iterable<Asset>>() {
			
			@Override
			public Iterable<Asset> answer(InvocationOnMock invocation) throws Throwable {
				List<Asset> assets = new ArrayList<Asset>();
				for (int i = 0; i < 1000; i++)
					assets.add(new TestAsset(UUID.randomUUID().toString(),"name"));
				return assets;
			}
		};
		
		when(proxy.browser().discover(asList(type))).thenAnswer(newAssets);
		
		
		
		final Repository repo = aService().with(proxy).get();

		final VirtualRepository virtual = repository(repo);

		final int load = 20;

		ExecutorService service = Executors.newFixedThreadPool(load);
		final CountDownLatch latch = new CountDownLatch(load);

		
		final Collection<Integer> conflicts = new ArrayList<>();
		
		for (int i = 0; i < load; i++) {
			
			service.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
						//simulate discovery and writes
						virtual.discover(type);
						
						//iterates after discovery, will interleave with other thread's discoveries
						for (Asset a : virtual)
							continue;
				
					}
					catch(Exception e) {
						e.printStackTrace();
						conflicts.add(1);
					}
					finally {
						latch.countDown();
					}
				}
			});
		}


		latch.await();
		
		assertTrue(conflicts.size()+" conflicts",conflicts.isEmpty());
		
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
	}

}
