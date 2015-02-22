package org.acme;

import static java.util.Arrays.*;
import static org.acme.TestMocks.*;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.AssetType;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.ServiceInspector;
import org.virtualrepository.spi.ServiceProxy;

public class InspectorTest {

	static AssetType.Private type1, type2, type3;
	static ServiceInspector inspector;

	@BeforeClass
	public static void stageService() {

		type1 = aType();
		type2 = aType();
		type3 = aType();
		
		ServiceProxy proxy = aProxy().with(
				 anImporterFor(type1, String.class), 
				 anImporterFor(type1, Integer.class), 
				 anImporterFor(type2, Boolean.class),
				 aPublisherFor(type2, Boolean.class), 
				 aPublisherFor(type2, Integer.class), 
				 aPublisherFor(type3, String.class)).get();
		
		RepositoryService service = aService().with(proxy).get();

		inspector = new ServiceInspector(service);
	}

	@Test
	public void findTypes() {

		assertEquals(asList(type1), inspector.returned(type1, type3));

		assertTrue(inspector.returned(type3).isEmpty());

		assertEquals(asList(type2), inspector.taken(type1, type2));

		assertTrue(inspector.taken(type1).isEmpty());

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
