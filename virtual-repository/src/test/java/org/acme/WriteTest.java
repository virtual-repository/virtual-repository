package org.acme;

import static java.util.Arrays.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualWriter;

public class WriteTest {

	@Test
	public void assetsCanBePublished() throws Exception {

		VirtualWriter<String> publisher = writerFor(String.class);

		Repository repository = repo().with(proxy().with(publisher)).get();

		Asset asset = testAsset().in(repository);

		VirtualRepository virtual = repositoryWith(repository);
		
		/////////////////////////////////////////////////////////////
		
		assertTrue(virtual.canPublish(asset).as(String.class));
		
		virtual.publish(asset, "hello");

		verify(publisher).publish(asset, "hello");
		
		//add transform
		
		assertFalse(virtual.canPublish(asset).as(Integer.class));
		
		
		virtual.extensions().transforms().add(asList(toString));
		
		assertTrue(virtual.canPublish(asset).as(String.class));

		virtual.publish(asset, 2);

		verify(publisher).publish(asset, "2");
		

	}


}
