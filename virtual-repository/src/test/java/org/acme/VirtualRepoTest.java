package org.acme;

import static org.acme.TestRepo.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.xml.namespace.QName;

import org.acme.TestRepo.TestAsset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.impl.DefaultVirtualRepository;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.RepositoryService;

public class VirtualRepoTest {
	
	@BeforeClass
	public static void setup() {
		
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","trace");
	}

	@Test
	public void assetsCanBeDiscovered() {
		
		TestRepo repo1 = new TestRepo();
		Asset a1 = repo1.asset().add();
		
		TestRepo repo2 = new TestRepo();
		
		Asset a2 = repo2.asset().add();
		Asset a3 = repo2.asset().of(anotherType).add();
		
		//test
		
		VirtualRepository repo = new DefaultVirtualRepository(repo1,repo2);
		
		int discovered = repo.discover(a1.type());
		
		assertEquals(2,discovered);
		
		Asset retrieved = repo.lookup(a1.id());
		
		assertEquals(a1,retrieved);
		
		retrieved = repo.lookup(a2.id());
		
		assertEquals(a2,retrieved);
		
		try {
			repo.lookup(a3.id());
			fail();
		}
		catch(IllegalStateException e) {}
		
		assertEquals(2, toCollection(repo).size());
	}
	
	@Test
	public void assetsCanBeDiscoveredIncrementally() {
		
		//stage repos
		
		TestRepo repo1 = new TestRepo();
		Asset a = repo1.asset().add();
		
		TestRepo repo2 = new TestRepo();
		repo2.asset().of(anotherType).add();
		
		//test
		
		VirtualRepository repo = new DefaultVirtualRepository(repo1,repo2);
		
		int before = repo.discover(a.type());
		
		repo2.asset().add();
		
		int after = repo.discover(a.type());
		
		assertEquals(before++,after);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void discoveryFailuresAreTolerated() throws Exception {
		
		//stage repos
		
		TestRepo repo1 = new TestRepo();
		Asset a = repo1.asset().add();
		
		
		//mock repository with failing browser 
		Browser failingBrowser = mock(Browser.class);
		when(failingBrowser.discover(anyList())).thenThrow(new Exception("oops"));
		
		RepositoryService failingRepo = mock(RepositoryService.class);
		when(failingRepo.name()).thenReturn(new QName("oops"));
		when(failingRepo.browser()).thenReturn(failingBrowser);
		
		//test
		
		VirtualRepository repo = new DefaultVirtualRepository(repo1,failingRepo);
		
		int discovered = repo.discover(a.type());
		
		assertEquals(1,discovered);
	}
	
	
	@Test
	public void retrievalFailsWithoutReader() {
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().with(10).add();
		
		VirtualRepository virtual = new DefaultVirtualRepository(repo);
		
		//no reader for integers
		try {
			virtual.retrieve(asset,Integer.class);
			fail();
		}
		catch(IllegalStateException e) {}
		
	}
	
	@Test
	public void assetsAreRetrieved() {
		
		final int data = 10;
		
		TestRepo repo = new TestRepo();
		
		//add reader for integers
		repo.addReader().yields(Integer.class);
		
		Asset asset = repo.asset().with(data).add();
		
		VirtualRepository virtual = new DefaultVirtualRepository(repo);
		
		int imported = virtual.retrieve(asset,Integer.class);
		
		assertEquals(data, imported);
	}
	
	
	@Test
	public void assetsCanBePublished() {
		
		TestRepo repo = new TestRepo();
		
		TestAsset asset = repo.asset().get();
		
		VirtualRepository virtual = new DefaultVirtualRepository(repo);
		
		virtual.publish(asset,"hello");
		
		String data = virtual.retrieve(asset, String.class);
		
		assertEquals("hello",data);
		
	}

}
