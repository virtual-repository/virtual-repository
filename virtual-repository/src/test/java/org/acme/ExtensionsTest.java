package org.acme;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.virtualrepository.VR.*;
import static smallgears.api.Apikit.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.virtualrepository.impl.Extensions;
import org.virtualrepository.spi.Transform;

public class ExtensionsTest {
	

	@Test
	public void loadExtensions() {
		
		Extensions extensions = extensions();
		
		extensions.load();
		
		assertTrue(extensions.size()>0);
	}
	
	
	@Test
	public void extensions_merge_transforms() {
		
		Extensions extensions = extensions().add(
									extension().transforms(toNum).get(),
									extension().transforms(toString).get()
								);
		
		//order-independent
		Set<Transform<?,?>> transforms = streamof(extensions.transforms()).collect(toSet());
		
		assertEquals(new HashSet<>(asList(toNum,toString)),transforms);
		
	}
	
}
