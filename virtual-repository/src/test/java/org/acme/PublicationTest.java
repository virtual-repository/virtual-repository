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
import org.virtualrepository.VirtualRepository.PublicationObserver;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualWriter;

public class PublicationTest {

	@Test
	public void is_predictable() {

		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repositoryWith(repo);

		assertTrue(virtual.canPublish(assetOfSomeType().in(repo)).as(String.class));
		
		assertFalse(virtual.canPublish(assetOfSomeType().in(repo)).as(Integer.class));

	}
	
	///////////////////////////////////////////////////////////////////////////////////// modes
	
	@Test
	public void works_in_blocking_mode() {

		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);
		
		Asset asset = assetOfSomeType().in(repo);

		vr.publish(asset).with("hello").blocking();
		
		assertSame("hello", vr.retrieve(asset).as(Object.class).blocking());

	}
	
	@Test @SneakyThrows
	public void works_in_nonblocking_mode() {

		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);
		
		Asset asset = assetOfSomeType().in(repo);
		
		Future<?> future = vr.publish(asset).with("hello").withoutBlocking();
		
		future.get(1,SECONDS);
		
		assertSame("hello", vr.retrieve(asset).as(Object.class).blocking());
	
	}
	
	
	@Test @SneakyThrows
	public void works_in_notifying_mode() {

		VirtualWriter<String> writer = writerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(writer)).get();
		
		Asset asset= assetOfSomeType().in(repo);
		
		doNothing().doThrow(new Exception()).when(writer).publish(asset,"hello");
		
		//////////////////////////////////////////////////////////////////////
		
		CountDownLatch latch = new CountDownLatch(2);
		
		VirtualRepository vr = repositoryWith(repo);
		
		PublicationObserver observer = mock(PublicationObserver.class);
		
		doAnswer(
			call-> {
				latch.countDown();
				return null;
			}
		)
		.when(observer).onSuccess();
		
		doAnswer(
				call-> {
					latch.countDown();
					return null;
				}
			)
		.when(observer).onError(any(Exception.class));
		
		vr.publish(asset).with("hello").notifying(observer);
		
		vr.publish(asset).with("hello").notifying(observer);
		
		assertTrue(latch.await(1,SECONDS));
		
		
	}


	///////////////////////////////////////////////////////////////////////////////////// errors
		
	
	@Test
	public void fails_without_writer() {
	
		Repository repo = repoThatTakesSomeTypeAnd(String.class);
		
		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);
		
		// no reader for integers
		try {
			vr.publish(assetOfSomeType().in(repo)).with(10).blocking();
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		
		try {
			vr.publish(assetOfSomeType().in(repo)).with(10).withoutBlocking();
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		
		
		try {
			vr.publish(assetOfSomeType().in(repo)).with(10).notifying(new PublicationObserver(){});
			fail();
		}
		catch(IllegalStateException its_thrown) {}
		
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void fails_without_repository() {

		VirtualRepository vr = repositoryWith(repoThatTakesSomeTypeAnd(String.class));

		// no repo for asset
		vr.publish(assetOfSomeType().in(null)).with(10).blocking();
		
		//no need to test in other modes, the previous test covers all sync exceptions
		
	}
	
	
	@Test @SneakyThrows 
	public void fails_for_timeout() {
		
		VirtualWriter<String> writer = writerFor(some_type,String.class);
		
		Repository repo = repo().with(proxy().with(writer)).get();
		
		//takes quite some time
		doAnswer((call)->{
			Thread.sleep(1000);
			return null;
		}).when(writer).publish(any(Asset.class),anyObject());

		VirtualRepository vr = repositoryWith(repo);

		try {
			vr.publish(assetOfSomeType().in(repo)).with("hello").timeout(ofMillis(100)).blocking();
			fail();
		}
		catch(Exception e) {
			assertTrue(e.getCause() instanceof TimeoutException);
		}
		
		try {
			vr.publish(assetOfSomeType().in(repo)).with("hello").withoutBlocking().get(100,MILLISECONDS);
			fail();
		}
		catch(TimeoutException e) {}
		
		
		CountDownLatch latch = new CountDownLatch(1);
		
		PublicationObserver observer = mock(PublicationObserver.class);
		
		doAnswer(
				call-> {
					latch.countDown();
					return null;
				}
			)
		.when(observer).onError(any(TimeoutException.class));
		
		vr.publish(assetOfSomeType().in(repo)).with("hello").timeout(ofMillis(100)).notifying(observer);
		
		assertTrue(latch.await(1,SECONDS));
		
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void uses_transforms() throws Exception {

		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////////////////
		
		VirtualRepository vr = repositoryWith(repo);
		
		Asset asset = assetOfSomeType().in(repo);

		assertFalse(vr.canPublish(asset).as(Integer.class));
		
		//add transform
		Transform<?,?> int2string = transform(some_type).from(Integer.class).to(String.class).with(String::valueOf);
		
		vr.transforms().add(int2string);
		
		assertTrue(vr.canPublish(asset).as(Integer.class));

	}

	@Test
	public void uses_subtyping() throws Exception {
		
		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo);

		AssetType subtype = type().specialises(some_type);
		
		Asset subtypeAsset = testAsset().of(subtype).in(repo);

		//cannot publish subtype as Integer
		assertTrue(vr.canPublish(subtypeAsset).as(String.class));
		
	}
	
	@Test
	public void uses_subtyping_and_transforms() throws Exception {
		
		Repository repo = repoThatTakesSomeTypeAnd(String.class);

		//////////////////////////////////////////////////////////

		VirtualRepository vr = repositoryWith(repo);

		AssetType subtype = type().specialises(some_type);
		
		Asset subtypeAsset = testAsset().of(subtype).in(repo);

		//cannot publish subtype as Integer
		assertFalse(vr.canPublish(subtypeAsset).as(Integer.class));

		//addtransform
		Transform<?,?> int2string = transform(some_type).from(Integer.class).to(String.class).with(String::valueOf);
		
		vr.transforms().add(int2string);
		
		//can now publish subtype as integer (with supertype publisher)
		assertTrue(vr.canPublish(subtypeAsset).as(Integer.class));
		
	}
}
