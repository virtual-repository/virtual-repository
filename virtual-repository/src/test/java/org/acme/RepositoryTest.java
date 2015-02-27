package org.acme;

import static java.util.Arrays.*;
import static org.acme.TestMocks.*;
import static org.junit.Assert.*;
import static org.virtualrepository.Types.*;
import static org.virtualrepository.common.Utils.*;
import static org.virtualrepository.common.Utils.Comparison.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.spi.VirtualProxy;

public class RepositoryTest {

	static AssetType type1, type2, type3;

	static Repository repository;
	
	@BeforeClass
	public static void stageService() {

		type1 = aType();
		type2 = aType();
		type3 = aType();
		
		VirtualProxy proxy = aProxy().with(
				 aReaderFor(type1, String.class), 
				 aReaderFor(type1, Integer.class), 
				 aReaderFor(type2, Boolean.class),
				 aWriterFor(type2, Boolean.class), 
				 aWriterFor(type2, Integer.class), 
				 aWriterFor(type3, String.class)).get();
		
		repository = aService().with(proxy).get();

	}
	
	@Test
	public void type_campare() {
		
		assertEquals(SUPERTYPE,compare(any,type1));
		assertEquals(SUBTYPE,compare(type1,any));
		assertEquals(EQUALS,compare(type1,type1));
		assertEquals(EQUALS,compare(any,any));
		assertEquals(UNRELATED,compare(type1,type2));
		
	}
	
//	@Test
//	public void readers_compare() {
//		
//		assertEquals(EQUALS,compare(aReaderFor(type1, String.class),aReaderFor(type1, String.class)));
//		assertEquals(EQUALS,compare(aReaderFor(any, String.class),aReaderFor(any, String.class)));
//		assertEquals(SUBTYPE,compare(aReaderFor(type1, String.class),aReaderFor(any, String.class)));
//		assertEquals(SUPERTYPE,compare(aReaderFor(any, String.class),aReaderFor(type1, String.class)));
//		assertEquals(SUBTYPE,compare(aReaderFor(type1, String.class),aReaderFor(type1, Object.class)));
//		assertEquals(SUPERTYPE,compare(aReaderFor(type1, Object.class),aReaderFor(type1, String.class)));
//		assertEquals(UNRELATED,compare(aReaderFor(type1, Boolean.class),aReaderFor(type1, String.class)));
//	
//		//no point testing writers too, logic is shared from accessor.
//	}
//	
//	
//	@Test
//	public void readers_partially_ordered() {
//		
//		Accessor<?> r1 = aReaderFor(type1, String.class);
//		Accessor<?> r11 = aReaderFor(type1, String.class);
//		Accessor<?> r2 = aReaderFor(type1, Object.class);
//		Accessor<?> r3 = aReaderFor(type1, Boolean.class);
//		
//		Comparator<Accessor<?>> comparator = comparing(a->a.api(), (a1,a2)-> a1.isAssignableFrom(a2) ? 1 :-1);
//			
//		List<Accessor<?>> sorted = asList(r1,r11,r2,r3);
//		
//		sorted.sort(comparator);
//		
//		assertEquals(asList(r3,r1,r11,r2),sorted);
//	
//		//no point testing writers too, logic is shared from accessor.
//	}
	
	@Test
	public void findReturnedTypes() {
		
		assertEquals(asList(type1,type2), repository.returned());
		assertEquals(asList(type1), repository.returned(type1, type3));
		assertTrue(repository.returns(type1,type2));
		assertFalse(repository.returns(type3));
		

	}
	
	
	@Test
	public void findTakenTypes() {
		
		assertEquals(asList(type2,type3), repository.taken());
		assertEquals(asList(type2), repository.taken(type1, type2));
		assertTrue(repository.takes(type2,type3));
		assertFalse(repository.takes(type1));
		

	}

	@Test
	public void findReaders() {

		assertEquals(2, repository.readersFor(type1).size());
		assertEquals(1, repository.readersFor(type2).size());
		assertTrue(repository.readersFor(type3).isEmpty());

	}
	
	@Test
	public void findWriters() {

		assertTrue(repository.writersFor(type1).isEmpty());
		assertEquals(2, repository.writersFor(type2).size());
		assertEquals(1, repository.writersFor(type3).size());

	}

	@Test
	public void findReadersByApi() {

		assertFalse(repository.readersFor(type1, String.class).isEmpty());

		assertFalse(repository.readersFor(type1, Integer.class).isEmpty());

		assertTrue(repository.readersFor(type1, Boolean.class).isEmpty());

		assertFalse(repository.readersFor(type2, Boolean.class).isEmpty());

		assertTrue(repository.readersFor(type2, Integer.class).isEmpty());

		assertTrue(repository.readersFor(type3, String.class).isEmpty());
	
	}
	
	@Test
	public void findWritersByApi() {

		assertFalse(repository.writersFor(type2, Boolean.class).isEmpty());
		
		assertFalse(repository.writersFor(type2, Integer.class).isEmpty());
		
		assertTrue(repository.writersFor(type2, String.class).isEmpty());
		
		assertFalse(repository.writersFor(type3, String.class).isEmpty());
		
		assertTrue(repository.writersFor(type3, Integer.class).isEmpty());
		
		assertTrue(repository.writersFor(type1, String.class).isEmpty());
		
	}
}
