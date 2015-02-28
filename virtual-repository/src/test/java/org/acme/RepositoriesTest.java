package org.acme;

import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.virtualrepository.VR.*;

import org.junit.Test;
import org.virtualrepository.Repositories;
import org.virtualrepository.Repository;

public class RepositoriesTest {
	

	@Test
	public void addRepository() {
		

		Repository repo = repo().get();
		
		Repositories repos = repositories(repo);

		assertTrue(repos.size()==1);
		assertTrue(repos.has(repo));
		
	}
	
	@Test
	public void loadRepository() {
		
		Repositories repos = repositories();
		
		repos.load();
		
		assertTrue(repos.size()>0);
	}
	
	
	@Test
	public void repositoriesMustBeUniquelyNamed() {
		
		Repositories services = repositories(repo().name("test").get());
		
		assertEquals(1,services.size());
		
		services.add(repo().name("test").get());
		
		assertEquals(1,services.size());
	}
	
}
