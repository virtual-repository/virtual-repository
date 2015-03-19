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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static smallgears.virtualrepository.VR.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.Repository;
import smallgears.virtualrepository.VirtualRepository;
import smallgears.virtualrepository.VirtualRepository.DiscoveryObserver;

public class DiscoveryTest {
	

	@Test
	public void works_in_blocking_mode() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();

		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(asList(a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository repo = repositoryWith(repo1, repo2);

		int discovered = repo.discover(some_type).blocking();

		assertSame(2,discovered);
		assertSame(2,repo.size());

		assertEquals(a1, repo.lookup(a1.id()).get());
		assertEquals(a2,repo.lookup(a2.id()).get());

		//negative case
		assertFalse(repo.lookup("bad").isPresent());
	}

	
	@Test
	public void can_discover_assets_of_any_type() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeOtherType();

		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeOtherType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_other_type))).thenReturn(asList(a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository repo = repositoryWith(repo1, repo2);

		int discovered = repo.discover().blocking();

		assertSame(2,discovered);
		assertSame(2,repo.size());

		assertEquals(a1, repo.lookup(a1.id()).get());
		assertEquals(a2,repo.lookup(a2.id()).get());

	}
	
	
	@Test
	public void can_be_incremental() throws Exception {

		Repository repo = repoThatReadsSomeType();

		Asset a1 = assetOfSomeType().in(repo);
		Asset a2 = assetOfSomeType().in(repo);
		
		when(repo.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1))
																.thenReturn(asList(a1,a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo);

		int discovered = vr.discover(some_type).blocking();

		assertEquals(1, discovered);

		discovered = vr.discover(some_type).blocking();

		assertEquals(1, discovered);
	}

	
	
	@Test
	public void failures_are_tolerated() throws Exception {

		Repository goodrepo = repoThatReadsSomeType();
		Repository badrepo = repoThatReadsSomeType();
		
		when(goodrepo.proxy().browser().discover(asList(some_type))).thenReturn(emptyList());
		when(badrepo.proxy().browser().discover(asList(some_type))).thenThrow(new Exception());

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(goodrepo, badrepo);

		vr.discover(some_type).blocking();
	}
	
	@Test
	public void can_be_restricted() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(asList(a2));

		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo1, repo2);

		vr.discover(some_type).over(repo1).blocking();
		
		assertTrue(vr.lookup(a1.id()).isPresent());
		assertFalse(vr.lookup(a2.id()).isPresent());
	}
	
	@Test
	public void respects_timeout() throws Exception {

		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		Asset a1 = assetOfSomeType().in(repo1);
		Asset a2 = assetOfSomeType().in(repo2);
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(asList(a1));
		when(repo2.proxy().browser().discover(asList(some_type))).thenAnswer($->{Thread.sleep(100); return asList(a2);});
	
		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo1,repo2);

		vr.discover(some_type).timeout(ofMillis(50)).blocking();
		
		assertTrue(vr.lookup(a1.id()).isPresent());
		assertFalse(vr.lookup(a2.id()).isPresent());
	}
	

	@Test
	public void is_threadsafe() throws Exception {

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
		
		VirtualRepository vr = repositoryWith(repo1,repo2);

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
	public void works_in_asynchronous_mode() throws Exception {
		
		Repository repo = repoThatReadsSomeType();
		
		Asset a = assetOfSomeType().in(repo);
		
		when(repo.proxy().browser().discover(asList(some_type))).thenAnswer($->{Thread.sleep(100); return asList(a);});
	
		///////////////////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo);

		Future<Integer> future = vr.discover(some_type).withoutBlocking();
		
		assertFalse(vr.lookup(a.id()).isPresent());
		
		future.get(1, SECONDS);
		
		assertTrue(vr.lookup(a.id()).isPresent());
	}
	
	@Test
	public void works_in_notifying_mode() throws Exception {

		//two repos, thousand assets each, fifty discovery threads.
		
		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		List<Asset> assets1 = range(0,100).mapToObj(__->assetOfSomeType().in(repo1)).collect(toList());
		List<Asset> assets2 = range(0,100).mapToObj(__->assetOfSomeType().in(repo2)).collect(toList());
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(assets1);
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(assets2);
		
		///////////////////////////////////////////////////////////////////////

		CountDownLatch latch = new CountDownLatch(1);
	
		VirtualRepository vr = repositoryWith(repo1,repo2);
	
		@SuppressWarnings("all")
		DiscoveryObserver observer = (DiscoveryObserver) mock(DiscoveryObserver.class);
		
		doAnswer(call-> {
			latch.countDown();
			return null;
		}).when(observer).onCompleted();
		
		//clunky but: confirm that assets are already in repo when they're notified
		doAnswer(call-> {
			assertNotNull(vr.lookup(call.getArgumentAt(0,Asset.class).id()));
			return null;
		})
		.when(observer).onNext(any(Asset.class));
		
		
		vr.discover(some_type).notifying(observer);
		
		latch.await(1,SECONDS);
		
		verify(observer,times(200)).onNext(any(Asset.class));
		
	}
	

	@Test
	public void supports_reactive_programming() throws Exception {

		//two repos, thousand assets each, fifty discovery threads.
		
		Repository repo1 = repoThatReadsSomeType();
		Repository repo2 = repoThatReadsSomeType();
		
		List<Asset> assets1 = range(0,100).mapToObj(__->assetOfSomeType().in(repo1)).collect(toList());
		List<Asset> assets2 = range(0,100).mapToObj(__->assetOfSomeType().in(repo2)).collect(toList());
		
		when(repo1.proxy().browser().discover(asList(some_type))).thenReturn(assets1);
		when(repo2.proxy().browser().discover(asList(some_type))).thenReturn(assets2);
		
		///////////////////////////////////////////////////////////////////////

	
		VirtualRepository vr = repositoryWith(repo1,repo2);
	
		Observable<Asset> assets = Observable.create(o->{
		
			vr.discover(some_type).notifying(new DiscoveryObserver() {
				public void onCompleted() {o.onCompleted();}
				public void onNext(Asset a) {o.onNext(a);}
				
			});
		});
		
		CountDownLatch latch = new CountDownLatch(1);
		
		assets.count().subscribe(e->{
			assertTrue(e==200);
			latch.countDown();
		});
		
		latch.await(1,TimeUnit.SECONDS);
		
	}
}
