package org.acme;

import static java.util.Arrays.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;
import static org.virtualrepository.common.Utils.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

public class ReadWriteTest {

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
	public void retrievalFailsWithoutReader() {

		Repository repository = repo().get();

		Asset asset = testAsset().in(repository);

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

		VirtualReader<Integer> reader = readerFor(Integer.class);
		
		Repository repository = repo().with(proxy().with(reader)).get();

		Asset asset = testAsset().in(repository);

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

		VirtualReader<Integer> reader = readerFor(subtype,Integer.class);
		
		Repository repository = repo().with(proxy().with(reader)).get();

		Asset asset = testAsset().of(subtype).in(repository);

		when(reader.retrieve(asset)).thenReturn(data);

		//////////////////////////////////////////////////////////
		
		VirtualRepository virtual = repository(repository);

		assertFalse(virtual.canRetrieve(asset,String.class));

		//adding transform for supertype: e.g. think converts inputstream to dom
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canRetrieve(asset,String.class));
		
		assertEquals(String.valueOf(data), virtual.retrieve(asset, String.class));
	}

	@Test
	public void assetsCanBePublished() throws Exception {

		VirtualWriter<String> publisher = writerFor(String.class);

		Repository repository = repo().with(proxy().with(publisher)).get();

		Asset asset = testAsset().in(repository);

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


}
