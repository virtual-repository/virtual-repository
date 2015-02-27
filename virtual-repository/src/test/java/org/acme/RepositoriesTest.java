package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.virtualrepository.VR.*;

import org.junit.Test;
import org.virtualrepository.Repositories;
import org.virtualrepository.Repository;

public class RepositoriesTest {
	

	@Test
	public void addRepository() {
		

		Repository service = aService().get();
		
		Repositories services = repositories(service);
		
		assertTrue(services.has(service.name()));
		
		assertEqualElements(services,singleton(service));
	}
	
	@Test
	public void loadRepository() {
		
		Repositories repos = repositories();
		
		repos.load();
		
		assertTrue(repos.size()>0);
	}
	
	
	@Test
	public void repositoriesMustBeUniquelyNamed() {
		
		Repositories services = repositories(aService().name("test").get());
		
		assertEquals(1,services.size());
		
		services.add(aService().name("test").get());
		
		assertEquals(1,services.size());
	}
	
}
