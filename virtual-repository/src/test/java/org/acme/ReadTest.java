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
	
	
	@Test
	public void works_in_blocking_mode() throws Exception {

		Repository repo = repoThatReadsSomeTypeWith("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);

		assertTrue(virtual.canRetrieve(assetOfSomeType().in(repo),String.class));
		
		String retrieved = virtual.retrieve(assetOfSomeType().in(repo)).as(String.class).blocking();
		
		assertSame("hello", retrieved);

	}
	
	
	@Test
	public void works_in_nonblocking_mode() throws Exception {

		Repository repo = repoThatReadsSomeTypeWith("hello");

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);
		
		Future<String> future = virtual.retrieve(assetOfSomeType().in(repo)).as(String.class).withoutBlocking();
		
		assertSame("hello", future.get(1,SECONDS));
		
	}
	
	@Test
	public void works_in_notifying_mode() throws Exception {

		Repository repo = repoThatReadsSomeTypeWith("hello");
		
		//////////////////////////////////////////////////////////////////////
		
		CountDownLatch latch = new CountDownLatch(2);
		
		VirtualRepository virtual = repositoryWith(repo);
		
		@SuppressWarnings("all")
		ContentObserver<String> stringobserver = (ContentObserver) mock(ContentObserver.class);
		
		Asset asset= assetOfSomeType().in(repo);
		
		doAnswer(
			call-> {
				latch.countDown();
				return null;
			}
		)
		.when(stringobserver).onSuccess("hello");
		
		virtual.retrieve(asset).as(String.class).notifying(stringobserver);
		
		@SuppressWarnings("all")
		ContentObserver<Integer> intobserver = (ContentObserver) mock(ContentObserver.class);
		
		doAnswer(
				call-> {
					latch.countDown();
					return null;
				}
			)
		.when(intobserver).onError(any(Exception.class));
		
		virtual.retrieve(asset).as(Integer.class).notifying(intobserver);
		
		assertTrue(latch.await(1,SECONDS));
		
		
	}
	

	@Test(expected=IllegalStateException.class)
	public void fails_without_reader() {

		Repository repo = repoThatReadsSomeType();

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);

		// no reader for integers
		vr.retrieve(assetOfSomeType().in(repo)).as(Integer.class).blocking();
		
	}
	
	
	@Test
	public void uses_extensions() throws Exception {

		Repository repository = repoThatReadsSomeTypeWith("10");

		//////////////////////////////////////////////////////////////////////
		
		Asset asset = testAsset().in(repository);

		VirtualRepository virtual = repositoryWith(repository);

		//add transform
		
		assertFalse(virtual.canRetrieve(asset,Integer.class));
		
		//add extension
		
		virtual.extensions().transforms().add(asList(toNum));
		
		assertTrue(virtual.canRetrieve(asset,Integer.class));
		
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

		assertFalse(virtual.canRetrieve(asset,String.class));

		//adding transform for supertype: e.g. think converts inputstream to dom
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canRetrieve(asset,String.class));
		
		assertEquals(String.valueOf(data), virtual.retrieve(asset).as(String.class).blocking());
	}

}
