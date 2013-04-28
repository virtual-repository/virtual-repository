package org.acme;

import static java.util.Collections.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import org.fao.virtualrepository.impl.Repositories;
import org.junit.Test;

public class RepositoriesTest {

	@Test
	public void addRepository() {
		
		Repositories repos = new Repositories();
		
		TestRepo repo = new TestRepo();
		
		repos.add(repo);
		
		assertTrue(repos.contains(repo.name()));
		
		assertEqualElements(toCollection(repos),singleton(repo));
	}
	
	@Test
	public void loadRepository() {
		
		Repositories repos = new Repositories();
		
		repos.load();
		
		assertFalse(toCollection(repos).isEmpty());
	}
	
	
	@Test
	public void repositoriesMustBeUniquelyNamed() {
		
		Repositories repos = new Repositories();
		
		repos.add(new TestRepo("test"));
		
		assertEquals(1,toCollection(repos).size());
		
		repos.add(new TestRepo("test"));
		
		assertEquals(1,toCollection(repos).size());
	}
	
	
	
}
