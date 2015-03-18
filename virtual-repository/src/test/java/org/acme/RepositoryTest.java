package org.acme;

import static java.util.Arrays.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.spi.VirtualProxy;

public class RepositoryTest {

	static AssetType type1, type2, type3;

	static Repository repository;
	
	@BeforeClass
	public static void stageRepository() {

		type1 = type();
		type2 = type();
		type3 = type();
		
		VirtualProxy proxy = proxy().with(
				 readerFor(type1, String.class), 
				 readerFor(type1, Integer.class), 
				 readerFor(type2, Boolean.class),
				 writerFor(type2, Boolean.class), 
				 writerFor(type2, Integer.class), 
				 writerFor(type3, String.class)).get();
		
		repository = repo().with(proxy).get();

	}
	
	@Test
	public void findReturnedTypes() {
		
		assertEquals(asList(type1,type2), repository.disseminated());
		assertEquals(asList(type1), repository.disseminated(type1, type3));
		assertTrue(repository.disseminates(type1,type2));
		assertFalse(repository.disseminates(type3));
		

	}
	
	
	@Test
	public void findTakenTypes() {
		
		assertEquals(asList(type2,type3), repository.ingested());
		assertEquals(asList(type2), repository.ingested(type1, type2));
		assertTrue(repository.ingests(type2,type3));
		assertFalse(repository.ingests(type1));
		

	}

}
