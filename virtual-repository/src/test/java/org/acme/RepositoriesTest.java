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
		
		Repositories repositories = repositories(repo().name("test").get());
		
		assertEquals(1,repositories.size());
		
		repositories.add(repo().name("test").get());
		
		assertEquals(1,repositories.size());
	}
	
	@Test
	public void repo_can_shutdown() throws Exception {
		
		repository().shutdown();
		
	}
}
