package org.acme;

import static java.util.Arrays.*;
import static org.acme.TestMocks.*;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.impl.ServiceInspector;
import org.virtualrepository.spi.VirtualProxy;

public class InspectorTest {

	static AssetType type1, type2, type3;
	static ServiceInspector inspector;

	static Repository repository;
	
	@BeforeClass
	public static void stageService() {

		type1 = aType();
		type2 = aType();
		type3 = aType();
		
		VirtualProxy proxy = aProxy().with(
				 anImporterFor(type1, String.class), 
				 anImporterFor(type1, Integer.class), 
				 anImporterFor(type2, Boolean.class),
				 aPublisherFor(type2, Boolean.class), 
				 aPublisherFor(type2, Integer.class), 
				 aPublisherFor(type3, String.class)).get();
		
		repository = aService().with(proxy).get();

		inspector = new ServiceInspector(repository);
	}
	
	@Test
	public void findTypes() {
		
		assertEquals(asList(type1,type2), repository.returned());
		assertEquals(asList(type1), repository.returned(type1, type3));
		assertTrue(repository.returns(type1,type2));
		assertFalse(repository.returns(type3));
		
		assertEquals(asList(type2,type3), repository.taken());
		assertEquals(asList(type2), repository.taken(type1, type2));
		assertTrue(repository.takes(type2,type3));
		assertFalse(repository.takes(type1));
		

	}

	@Test
	public void findAccessors() {

		assertEquals(2, inspector.importersFor(type1).size());

		assertEquals(1, inspector.importersFor(type2).size());

		assertTrue(inspector.importersFor(type3).isEmpty());

		assertTrue(inspector.publishersFor(type1).isEmpty());

		assertEquals(2, inspector.publishersFor(type2).size());

		assertEquals(1, inspector.publishersFor(type3).size());

	}

	@Test
	public void findApis() {

		inspector.importerFor(type1, String.class);

		inspector.importerFor(type1, Integer.class);

		try {
			inspector.importerFor(type1, Boolean.class);
			fail();
		} catch (Exception e) {
		}

		inspector.importerFor(type2, Boolean.class);

		try {
			inspector.importerFor(type2, Integer.class);
			fail();
		} catch (Exception e) {
		}

		try {
			inspector.importerFor(type3, String.class);
			fail();
		} catch (Exception e) {
		}

		
		
		inspector.publisherFor(type2, Boolean.class);
		
		inspector.publisherFor(type2, Integer.class);
		
		try {
			inspector.publisherFor(type2, String.class);
			fail();
		} catch (Exception e) {
		}
		
		inspector.publisherFor(type3, String.class);
		
		try {
			inspector.publisherFor(type3, Integer.class);
			fail();
		} catch (Exception e) {
		}
		
		try {
			inspector.publisherFor(type1, String.class);
			fail();
		} catch (Exception e) {
		}
	}
}
