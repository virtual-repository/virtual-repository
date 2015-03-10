package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualProxy;

public class DiscoveryTest {
	
	AssetType type = type();
	AssetType type2 = type();

	@Test
	public void assetsCanBeDiscovered() throws Exception {

		//setup
		
		VirtualProxy proxy1 = proxy().with(readerFor(type)).get();
		VirtualProxy proxy2 = proxy().with(readerFor(type)).get();

		Repository repo1 = repo().with(proxy1).get();
		Repository repo2 = repo().with(proxy2).get();

		Asset a1 = testAsset().of(type).in(repo1);
		
		Asset a2 = testAsset().of(type).in(repo2);
		
		when(proxy1.browser().discover(asList(type))).thenReturn(singleton(a1));
		when(proxy2.browser().discover(asList(type))).thenReturn(singleton(a2));

		// test

		VirtualRepository repo = repository(repo1, repo2);

		int discovered = repo.discover(type).now();

		assertTrue(discovered==2);
		
		assertTrue(repo.size()==2);

		assertEquals(a1, repo.lookup(a1.id()).get());

		assertEquals(a2,repo.lookup(a2.id()).get());

		Asset a3 = testAsset().of(type2).in(repo2);
		
		assertFalse(repo.lookup(a3.id()).isPresent());

	}

	@Test
	public void assetsCanBeDiscoveredIncrementally() throws Exception {

		VirtualProxy proxy = proxy().with(readerFor(type)).get();
		Repository repo = repo().with(proxy).get();

		Asset a1 = testAsset().of(type).in(repo);
		Asset a2 = testAsset().of(type).in(repo);
		
		when(proxy.browser().discover(asList(type))).thenReturn(singleton(a1)).thenReturn(asList(a1,a2));

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

		Asset a = testAsset().of(type).in(repository);

		when(proxy.browser().discover(asList(type))).thenReturn(singleton(a));
		when(failing.proxy().browser().discover(anyListOf(AssetType.class))).thenThrow(new Exception());

		// test

		VirtualRepository virtual = repository(repository, failing);

		int discovered = virtual.discover(type).now();

		assertEquals(1, discovered);
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
					assets.add(asset().name("name").justDiscovered());
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
