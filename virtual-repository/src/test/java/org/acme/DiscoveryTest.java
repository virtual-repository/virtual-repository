package org.acme;

import static java.time.Duration.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VR.AssetClause;
import org.virtualrepository.VirtualRepository;

public class DiscoveryTest {
	
	AssetType some_type = type();
	AssetType some_other_type = type();

	@Test
	public void assets_can_be_discovered() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();

		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(asList(a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository repo = repository(repo1, repo2);

		int discovered = repo.discover(some_type).blocking();

		assertSame(2,discovered);
		assertSame(2,repo.size());

		assertEquals(a1, repo.lookup(a1.id()).get());
		assertEquals(a2,repo.lookup(a2.id()).get());

		//negative case
		assertFalse(repo.lookup("bad").isPresent());

	}

	@Test
	public void discovery_can_be_incremental() throws Exception {

		Repository repo = repoThatReadsSomeType();

		Asset a1 = assetOfSomeType().in(repo);
		Asset a2 = assetOfSomeType().in(repo);
		
		when(repo.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1))
																.thenReturn(asList(a1,a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repository(repo);

		int discovered = vr.discover(some_type).blocking();

		assertEquals(1, discovered);

		discovered = vr.discover(some_type).blocking();

		assertEquals(1, discovered);
	}

	
	
	@Test
	public void discovery_failures_are_tolerated() throws Exception {

		Repository goodrepo = repoThatReadsSomeType();
		Repository badrepo = repoThatReadsSomeType();
		
		when(goodrepo.proxy().browser().discover(asList(some_type))).thenReturn(emptyList());
		when(badrepo.proxy().browser().discover(asList(some_type))).thenThrow(new Exception());

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repository(goodrepo, badrepo);

		vr.discover(some_type).blocking();
	}
	
	@Test
	public void discovery_can_be_restricted() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(asList(a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repository(repo1, repo2);

		vr.discover(some_type).over(repo1).blocking();
		
		assertTrue(vr.lookup(a1.id()).isPresent());
		assertFalse(vr.lookup(a2.id()).isPresent());
	}
	
	@Test
	public void discovery_respects_timeout() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenAnswer($->{Thread.sleep(100); return asList(a2);});
	
		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repository(repo1,repo2);

		vr.discover(some_type).timeout(ofMillis(50)).blocking();
		
		assertTrue(vr.lookup(a1.id()).isPresent());
		assertFalse(vr.lookup(a2.id()).isPresent());
	}
	

	@Test
	public void discovery_is_threadsafe() throws Exception {

		//two repos, thousand assets each, fifty discovery threads.
		
		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		int amount = 1000;
		int load = 50;		
		
		List<Asset> assets1 = range(0,amount).mapToObj(__->assetOfSomeType().in(repo1)).collect(toList());
		List<Asset> assets2 = range(0,amount).mapToObj(__->assetOfSomeType().in(repo2)).collect(toList());
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(assets1);
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(assets2);
		
		///////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repository(repo1,repo2);

		ExecutorService service = newFixedThreadPool(load);
		CountDownLatch latch = new CountDownLatch(load);
		
		Collection<Integer> conflicts = new ArrayList<>();
		
		range(0,load).forEach($->{
			
			
			service.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						//simulate discovery and writes
						vr.discover(some_type).blocking();
						
						//iterates after discovery, will interleave with other thread's discoveries
						vr.stream().collect(toList());
				
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
		});

		latch.await();
		
		assertTrue(conflicts.size()+" conflicts",conflicts.isEmpty());
		assertEquals(2*amount,vr.size());
	}

	
	@Test
	public void discovery_can_be_asynchronous() throws Exception {
		
		Repository repo = repoThatReadsSomeType();
		
		Asset a = assetOfSomeType().in(repo);
		
		when(repo.proxy().browser().discover(asList(some_type))).thenAnswer($->{Thread.sleep(100); return asList(a);});
	
		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repository(repo);

		Future<Integer> future = vr.discover(some_type).withoutBlocking();
		
		assertFalse(vr.lookup(a.id()).isPresent());
		
		future.get(1, SECONDS);
		
		assertTrue(vr.lookup(a.id()).isPresent());
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Repository repoThatReadsSomeType() {
		
		return repo().with(proxy().with(readerFor(some_type))).get();
	}
	
	private AssetClause assetOfSomeType() {
		
		return testAsset().of(some_type);
	}
	

}
