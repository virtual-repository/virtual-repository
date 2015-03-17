package org.acme;

import static java.util.Arrays.*;
import static java.util.concurrent.TimeUnit.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;
import static org.virtualrepository.common.Utils.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.VirtualRepository.ContentObserver;
import org.virtualrepository.spi.VirtualReader;

public class ReadTest {
	

	///////////////////////////////////////////////////////////////////////////////////// modes
	
	@Test
	public void is_predictable() throws Exception {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);

		assertTrue(virtual.canRetrieve(assetOfSomeType().in(repo)).as(String.class));

	}
	
	@Test
	public void works_in_blocking_mode() throws Exception {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);

		String retrieved = virtual.retrieve(assetOfSomeType().in(repo)).as(String.class).blocking();
		
		assertSame("hello", retrieved);

	}
	
	
	@Test
	public void works_in_nonblocking_mode() throws Exception {

		Repository repo = repoThatReadsSomeTypeAndContains("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);
		
		Future<String> future = virtual.retrieve(assetOfSomeType().in(repo)).as(String.class).withoutBlocking();
		
		assertSame("hello", future.get(1,SECONDS));
		
	}
	
	@Test
	public void works_in_notifying_mode() throws Exception {

		VirtualReader<String> reader1 = readerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(reader1)).get();
		
		Exception exception = new Exception();
		
		when(reader1.retrieve(any(Asset.class))).thenReturn("hello").thenThrow(exception);
		
		//////////////////////////////////////////////////////////////////////
		
		CountDownLatch latch = new CountDownLatch(2);
		
		VirtualRepository virtual = repositoryWith(repo);
		
		@SuppressWarnings("all")
		ContentObserver<String> observer = (ContentObserver) mock(ContentObserver.class);
		
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
		
		virtual.retrieve(asset).as(String.class).notifying(observer);
		
		virtual.retrieve(asset).as(String.class).notifying(observer);
		
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
			                             .withoutBlocking();
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		

		try {
			vr.retrieve(assetOfSomeType().in(repo))
										.as(Integer.class)
										.notifying(new ContentObserver<Integer>() {});

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
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	@Test
	public void uses_extensions() throws Exception {

		Repository repository = repoThatReadsSomeTypeAndContains("10");

		//////////////////////////////////////////////////////////////////////
		
		Asset asset = testAsset().in(repository);

		VirtualRepository virtual = repositoryWith(repository);

		assertFalse(virtual.canRetrieve(asset).as(Integer.class));
		
		//add extension
		
		virtual.extensions().transforms().add(asList(toNum));
		
		assertTrue(virtual.canRetrieve(asset).as(Integer.class));
		
		assertSame(10, virtual.retrieve(asset).as(Integer.class).blocking());

	}
	
	
	
	
	
	@Test
	public void uses_subtyping() throws Exception {
		
		AssetType supertype = type();  // e.g. think generic xml
		AssetType subtype = type().specialises(supertype); //e.g. think X with XMl serialisation to stream
		
		assertTrue(ordered(subtype,supertype));
		
		final int data = 10;

		VirtualReader<Integer> reader = readerFor(subtype,Integer.class);
		
		Repository repository = repo().with(proxy().with(reader)).get();

		Asset asset = testAsset().of(subtype).in(repository);

		when(reader.retrieve(asset)).thenReturn(data);

		//////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repository);

		assertFalse(virtual.canRetrieve(asset).as(String.class));

		//adding transform for supertype: e.g. think converts inputstream to dom
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canRetrieve(asset).as(String.class));
		
		assertEquals(String.valueOf(data), virtual.retrieve(asset).as(String.class).blocking());
	}

}
