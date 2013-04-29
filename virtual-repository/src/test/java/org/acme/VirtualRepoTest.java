package org.acme;

import static org.acme.TestRepo.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.acme.TestRepo.TestAsset;
import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.impl.VirtualRepositoryImpl;
import org.fao.virtualrepository.processor.AssetProcessor;
import org.fao.virtualrepository.processor.Processors;
import org.fao.virtualrepository.spi.Browser;
import org.fao.virtualrepository.spi.RepositoryService;
import org.junit.BeforeClass;
import org.junit.Test;

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
		
		VirtualRepository repo = new VirtualRepositoryImpl(repo1,repo2);
		
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
		
		VirtualRepository repo = new VirtualRepositoryImpl(repo1,repo2);
		
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
		
		VirtualRepository repo = new VirtualRepositoryImpl(repo1,failingRepo);
		
		int discovered = repo.discover(a.type());
		
		assertEquals(1,discovered);
	}
	
	
	@Test
	public void retrievalFailsWithoutReader() {
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().with(10).add();
		
		VirtualRepository virtual = new VirtualRepositoryImpl(repo);
		
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
		
		VirtualRepository virtual = new VirtualRepositoryImpl(repo);
		
		int imported = virtual.retrieve(asset,Integer.class);
		
		assertEquals(data, imported);
	}
	
	
	@Test
	public void assetsCanBePublished() {
		
		TestRepo repo = new TestRepo();
		
		TestAsset asset = repo.asset().get();
		
		VirtualRepository virtual = new VirtualRepositoryImpl(repo);
		
		virtual.publish(asset,"hello");
		
		String data = virtual.retrieve(asset, String.class);
		
		assertEquals("hello",data);
		
	}
	
	
	@Test
	public void processAsset() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		AssetProcessor<Asset> processor = new AssetProcessor<Asset>() {
			
			@Override
			public void process(Asset asset) {
				latch.countDown();
			}
		};
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().add();
		
		@SuppressWarnings("unchecked")
		AssetType<Asset> type = (AssetType<Asset>) asset.type(); 
		
		Processors.add(type,processor);
		
		Processors.process(asset);
		
		latch.await();
	
	}

}
