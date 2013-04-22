package org.acme;

import static java.util.Collections.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import org.fao.virtualrepository.impl.Repositories;
import org.junit.Test;

public class RepositoriesTest {

	@Test
	public void addSource() {
		
		Repositories sources = new Repositories();
		
		TestRepository source = new TestRepository();
		
		sources.add(source);
		
		assertTrue(sources.contains(source.name()));
		
		assertEqualElements(sources.list(),singleton(source));
	}
	
	@Test
	public void loadSource() {
		
		Repositories sources = new Repositories();
		
		sources.load();
		
		assertFalse(sources.list().isEmpty());
	}
	
	
	@Test
	public void cannotAddSourcesWithSameName() {
		
		Repositories sources = new Repositories();
		
		sources.add(new TestRepository("somesource"));
		
		assertEquals(1,sources.list().size());
		
		sources.add(new TestRepository("somesource"));
		
		assertEquals(1,sources.list().size());
	}
	
	
	
}
