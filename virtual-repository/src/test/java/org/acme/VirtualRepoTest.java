package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;
import static org.virtualrepository.common.Utils.*;

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
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class VirtualRepoTest {

	AssetType type = type();
	AssetType type2 = type();

	@BeforeClass
	public static void setup() {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
	}

	@Test
	public void repo_can_shutdown() throws Exception {
		
		repository().shutdown();
		
	}
	
	@Test
	public void assetsCanBeDiscovered() throws Exception {

		//setup
		
		VirtualProxy proxy1 = proxy().with(readerFor(type)).get();
		VirtualProxy proxy2 = proxy().with(readerFor(type)).get();

		Repository repo1 = repo().with(proxy1).get();
		Repository repo2 = repo().with(proxy2).get();

		Asset a1 = asset().of(type).in(repo1);
		Asset a2 = asset().of(type).in(repo2);
		Asset a3 = asset().of(type2).in(repo2);

		when(proxy1.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1));
		when(proxy2.browser().discover(asList(type))).thenReturn((Iterable) singleton(a2));

		// test

		VirtualRepository repo = repository(repo1, repo2);

		int discovered = repo.discover(type).now();

		assertTrue(discovered==2);
		
		assertTrue(repo.size()==2);

		assertEquals(a1, repo.lookup(a1.id()).get());

		assertTrue(repo.lookup(a2.id()).isPresent());

		assertFalse(repo.lookup(a3.id()).isPresent());

	}

	@Test
	public void assetsCanBeDiscoveredIncrementally() throws Exception {

		VirtualProxy proxy = proxy().with(readerFor(type)).get();
		Repository repo = repo().with(proxy).get();

		Asset a1 = asset().of(type).in(repo);
		Asset a2 = asset().of(type).in(repo);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a1), (Iterable) asList(a1, a2));

		// test

		VirtualRepository vr = repository(repo);

		int size = vr.discover(type).now();

		assertEquals(1, size);

		size = vr.discover(type).now();

		assertEquals(1, size);
	}

	@Test
	public void discoveryFailuresAreTolerated() throws Exception {

		VirtualProxy proxy = proxy().with(readerFor(type)).get();
		Repository repository = repo().with(proxy).get();
		Repository failing = repo().get();

		Asset a = asset().of(type).in(repository);

		when(proxy.browser().discover(asList(type))).thenReturn((Iterable) singleton(a));
		when(failing.proxy().browser().discover(anyList())).thenThrow(new Exception());

		// test

		VirtualRepository virtual = repository(repository, failing);

		int discovered = virtual.discover(type).now();

		assertEquals(1, discovered);
	}

	@Test
	public void retrievalFailsWithoutReader() {

		Repository repository = repo().get();

		Asset asset = asset().in(repository);

		VirtualRepository virtual = repository(repository);

		// no reader for integers
		try {
			virtual.retrieve(asset, Integer.class);
			fail();
		} catch (IllegalStateException e) {
		}

	}
	
	
	@Test
	public void assets_can_be_retrieved() throws Exception {

		final int data = 10;

		VirtualReader<Asset, Integer> reader = readerFor(Integer.class);
		
		Repository repository = repo().with(proxy().with(reader)).get();

		Asset asset = asset().in(repository);

		when(reader.retrieve(asset)).thenReturn(data);

		//////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repository(repository);

		assertTrue(virtual.canRetrieve(asset,Integer.class));
		
		assertSame(data, virtual.retrieve(asset, Integer.class));
		
		//add transform
		
		assertFalse(virtual.canRetrieve(asset,String.class));
		
		//add extension
		
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canRetrieve(asset,String.class));
		
		assertEquals(String.valueOf(data), virtual.retrieve(asset, String.class));

	}
	
	@Test
	public void assets_can_be_retrieved_based_on_subtyping() throws Exception {
		
		AssetType supertype = type();  // e.g. think generic xml
		AssetType subtype = type().specialises(supertype); //e.g. think X with XMl serialisation to stream
		
		assertTrue(ordered(subtype,supertype));
		
		final int data = 10;

		VirtualReader<Asset, Integer> reader = readerFor(subtype,Integer.class);
		
		Repository repository = repo().with(proxy().with(reader)).get();

		Asset asset = asset().of(subtype).in(repository);

		when(reader.retrieve(asset)).thenReturn(data);

		//////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repository(repository);

		assertFalse(virtual.canRetrieve(asset,String.class));

		//adding transform for supertype: e.g. think converts inputstream to dom
		Transform<Asset,Integer,String> toString = 
				transform(Asset.class).type(supertype).from(Integer.class).to(String.class).with(String::valueOf);
		
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canRetrieve(asset,String.class));
		
		assertEquals(String.valueOf(data), virtual.retrieve(asset, String.class));
	}

	@Test
	public void assetsCanBePublished() throws Exception {

		VirtualWriter<Asset, String> publisher = writerFor(String.class);

		Repository repository = repo().with(proxy().with(publisher)).get();

		Asset asset = asset().in(repository);

		VirtualRepository virtual = repository(repository);
		
		/////////////////////////////////////////////////////////////
		
		assertTrue(virtual.canPublish(asset,String.class));
		
		virtual.publish(asset, "hello");

		verify(publisher).publish(asset, "hello");
		
		//add transform
		
		assertFalse(virtual.canPublish(asset,Integer.class));
		
		
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canPublish(asset,String.class));

		virtual.publish(asset, 2);

		verify(publisher).publish(asset, "2");
		

	}
	
	static class TestAsset extends Asset.Generic {
		
		static AssetType type = ()->"test";
		
		protected TestAsset(String id, String name) {
			super(type, id, name);
		}

	}
	

	@Test
	@SuppressWarnings("unused")
	public void discoveryCanProceedInParallel() throws Exception {

		VirtualProxy proxy = proxy().with(readerFor(type)).get();
		
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
		
		
		
		final Repository repo = repo().with(proxy).get();

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
