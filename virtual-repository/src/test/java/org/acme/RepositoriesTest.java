package org.acme;

import static java.util.Collections.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.impl.Repositories;
import org.fao.virtualrepository.spi.RepositoryDescription;
import org.junit.Test;

public class RepositoriesTest {

	@Test
	public void addSource() {
		
		Repositories repos = new Repositories();
		
		TestRepository repo = new TestRepository();
		
		repos.add(repo);
		
		assertTrue(repos.contains(repo.description().name()));
		
		assertEqualElements(repos.list(),singleton(repo));
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
		
		QName name = new QName("somesource");
		
		RepositoryDescription description = new RepositoryDescription(name);
		sources.add(new TestRepository(description));
		
		assertEquals(1,sources.list().size());
		
		sources.add(new TestRepository(description));
		
		assertEquals(1,sources.list().size());
	}
	
	
	
}
