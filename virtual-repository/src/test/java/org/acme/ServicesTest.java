package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.virtualrepository.impl.Services.*;

import org.junit.Test;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.Services;

public class ServicesTest {
	

	@Test
	public void addRepository() {
		

		RepositoryService service = aService().get();
		
		Services services = services(service);
		
		assertTrue(services.contains(service.name()));
		
		assertEqualElements(services,singleton(service));
	}
	
	@Test
	public void loadRepository() {
		
		Services repos = services();
		
		repos.load();
		
		assertTrue(repos.size()>0);
	}
	
	
	@Test
	public void repositoriesMustBeUniquelyNamed() {
		
		Services services = services(aService().name("test").get());
		
		assertEquals(1,services.size());
		
		services.add(aService().name("test").get());
		
		assertEquals(1,services.size());
	}
	
}
