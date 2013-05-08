package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.virtualrepository.impl.Services;
import org.virtualrepository.spi.RepositoryService;

public class ServicesTest {
	

	@Test
	public void addRepository() {
		
		Services services = new Services();
		
		RepositoryService service = aService().get();
		
		services.add(service);
		
		assertTrue(services.contains(service.name()));
		
		assertEqualElements(asList(services),singleton(service));
	}
	
	@Test
	public void loadRepository() {
		
		Services repos = new Services();
		
		repos.load();
		
		assertFalse(asList(repos).isEmpty());
	}
	
	
	@Test
	public void repositoriesMustBeUniquelyNamed() {
		
		Services services = new Services();
		
		services.add(aService().name("test").get());
		
		assertEquals(1,asList(services).size());
		
		services.add(aService().name("test").get());
		
		assertEquals(1,asList(services).size());
	}
	
}
