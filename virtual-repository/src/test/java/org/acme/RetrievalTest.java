package org.acme;

import static java.time.Duration.*;
import static java.util.concurrent.TimeUnit.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import lombok.SneakyThrows;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.VirtualRepository.RetrievalObserver;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualReader;

public class RetrievalTest {
	

	@Test
	public void is_predictable() {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);

		assertTrue(vr.canRetrieve(assetOfSomeType().in(repo)).as(String.class));
		
		assertFalse(vr.canRetrieve(assetOfSomeType().in(repo)).as(Integer.class));

	}
	
	///////////////////////////////////////////////////////////////////////////////////// modes
	
	
	@Test
	public void works_in_blocking_mode() {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);

		String retrieved = vr.retrieve(assetOfSomeType().in(repo)).as(String.class).blocking();
		
		assertSame("hello", retrieved);

	}
	
	
	@Test @SneakyThrows
	public void works_in_nonblocking_mode() {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);
		
		Future<String> future = vr.retrieve(assetOfSomeType().in(repo)).as(String.class).withoutBlocking();
		
		assertSame("hello", future.get(1,SECONDS));
		
	}
	
	@Test @SneakyThrows
	public void works_in_notifying_mode() {

		VirtualReader<String> reader = readerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(reader)).get();
		
		Exception exception = new Exception();
		
		when(reader.retrieve(any(Asset.class))).thenReturn("hello").thenThrow(exception);
		
		//////////////////////////////////////////////////////////////////////
		
		CountDownLatch latch = new CountDownLatch(2);
		
		VirtualRepository vr = repositoryWith(repo);
		
		@SuppressWarnings("all")
		RetrievalObserver<String> observer = (RetrievalObserver) mock(RetrievalObserver.class);
		
		Asset asset= assetOfSomeType().in(repo);
		
		doAnswer(
			call-> {
				latch.countDown();
				return null;
			}
		)
		.when(observer).onSuccess("hello");
		
		doAnswer(
				call-> {
					latch.countDown();
					return null;
				}
			)
		.when(observer).onError(exception);
		
		vr.retrieve(asset).as(String.class).notifying(observer);
		
		vr.retrieve(asset).as(String.class).notifying(observer);
		
		assertTrue(latch.await(1,SECONDS));
		
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////// errors
	

	@Test
	public void fails_without_reader() {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);

		// no reader for integers
		try {
			vr.retrieve(assetOfSomeType().in(repo)).as(Integer.class).blocking();
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		
		try {
			vr.retrieve(assetOfSomeType().in(repo))
			                             .as(Integer.class)
			                             .withoutBlocking();
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		
		
		try {
			vr.retrieve(assetOfSomeType().in(repo))
										.as(Integer.class)
										.notifying(new RetrievalObserver<Integer>() {});

			fail();
		}
		catch(IllegalStateException its_thrown) {}
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void fails_without_repository() {

		VirtualRepository vr = repositoryWith(repoThatReadsSomeType());

		// no repo for asset
		vr.retrieve(assetOfSomeType().in(null)).as(Integer.class).blocking();
		
		//no need to test in other modes, the previous test covers all sync exceptions
		
	}
	
	
	@Test @SneakyThrows 
	public void fails_for_timeout() {
		
		VirtualReader<String> reader = readerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(reader)).get();
		
		//takes quite some time
		when(reader.retrieve(any(Asset.class))).thenAnswer((call)->{
			Thread.sleep(1000);
			return null;
		});

		VirtualRepository vr = repositoryWith(repo);

		try {
			vr.retrieve(assetOfSomeType().in(repo)).as(String.class).timeout(ofMillis(100)).blocking();
			fail();
		}
		catch(Exception e) {
			assertTrue(e.getCause() instanceof TimeoutException);
		}
		
		try {
			vr.retrieve(assetOfSomeType().in(repo)).as(String.class).withoutBlocking().get(100,MILLISECONDS);
			fail();
		}
		catch(TimeoutException e) {}
		
		
		CountDownLatch latch = new CountDownLatch(1);
		
		@SuppressWarnings("all")
		RetrievalObserver<String> observer = (RetrievalObserver) mock(RetrievalObserver.class);
		
		doAnswer(
				call-> {
					latch.countDown();
					return null;
				}
			)
		.when(observer).onError(any(TimeoutException.class));
		
		vr.retrieve(assetOfSomeType().in(repo)).as(String.class).timeout(ofMillis(100)).notifying(observer);
		
		assertTrue(latch.await(1,SECONDS));
		
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	@Test
	public void uses_transforms() throws Exception {

		Repository repository = repoThatReadsSomeTypeAndContains("10");

		//////////////////////////////////////////////////////////////////////
		
		Asset asset = testAsset().of(some_type).in(repository);

		VirtualRepository vr = repositoryWith(repository);

		assertFalse(vr.canRetrieve(asset).as(Integer.class));
		
		//add transform
		
		Transform<?,?> string2int = transform(some_type).from(String.class).to(Integer.class).with(Integer::valueOf);
		
		vr.transforms().add(string2int);
		
		assertTrue(vr.canRetrieve(asset).as(Integer.class));

	}
	
	@Test
	public void uses_subtyping() throws Exception {

		VirtualReader<?> reader = readerFor(some_type,String.class);
				
		Repository repo = repo().with(proxy().with(reader)).get();

		VirtualRepository vr = repositoryWith(repo);

		//////////////////////////////////////////////////////////////////////
		
		AssetType subtype = type().specialises(some_type);
		
		Asset subtypeAsset = testAsset().of(subtype).in(repo);

		assertTrue(vr.canRetrieve(subtypeAsset).as(String.class));

	}
	
	
	@Test
	public void uses_transforms_and_subtyping() throws Exception {
	
		VirtualReader<?> readsStrings = readerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(readsStrings)).get();

		VirtualRepository vr = repositoryWith(repo);

		//////////////////////////////////////////////////////////////////////
		
		AssetType subtype = type().specialises(some_type);
		
		Asset asset = testAsset().of(subtype).in(repo);

		assertFalse(vr.canRetrieve(asset).as(Integer.class));
		
		Transform<?,?> transform = transform(some_type).from(String.class).to(Integer.class).with(Integer::valueOf);

		//adding transform for supertype
		vr.transforms().add(transform);
		
		//subtype->type->String->Integer
		
		assertTrue(vr.canRetrieve(asset).as(Integer.class));
	}

}
