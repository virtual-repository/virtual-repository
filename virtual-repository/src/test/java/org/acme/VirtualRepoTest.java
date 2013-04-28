package org.acme;

import static org.acme.TestRepo.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.impl.VirtualRepositoryImpl;
import org.fao.virtualrepository.processor.AssetProcessor;
import org.fao.virtualrepository.processor.Processors;
import org.junit.Test;

public class VirtualRepoTest {

	@Test
	public void assetsAreIngestedAndRetrieved() {
		
		//stage repos
		
		TestRepo repo1 = new TestRepo();
		Asset a1 = repo1.asset().add();
		
		TestRepo repo2 = new TestRepo();
		Asset a2 = repo2.asset().add();
		Asset a3 = repo2.asset().of(anotherType).add();
		
		//test
		
		VirtualRepository repo = new VirtualRepositoryImpl(repo1,repo2);
		
		repo.ingest(a1.type());
		
		Asset retrieved = repo.get(a1.id());
		
		assertEquals(a1,retrieved);
		
		retrieved = repo.get(a2.id());
		
		assertEquals(a2,retrieved);
		
		try {
			repo.get(a3.id());
			fail();
		}
		catch(IllegalStateException e) {}
		
		assertEquals(2, toCollection(repo).size());
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
