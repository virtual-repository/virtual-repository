package org.acme;

import static org.fao.virtualrepository.AssetType.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.csv.CSVAsset;
import org.fao.virtualrepository.impl.Repositories;
import org.fao.virtualrepository.impl.VirtualRepositoryImpl;
import org.fao.virtualrepository.processor.AssetProcessor;
import org.fao.virtualrepository.processor.Processors;
import org.junit.Test;

public class RepositoryTest {

	@Test
	public void ingest() {
		
		TestRepository repo = new TestRepository("test");
		
		CSVAsset asset = new CSVAsset("1", "asset-1",repo); 
		
		repo.addAssets(asset);
		
		Repositories repos = new Repositories();
		
		repos.add(repo);
	
		VirtualRepository repository = new VirtualRepositoryImpl(repos);
		
		repository.ingest(CSV);
		
		Asset retrieved = repository.get("1");
		
		assertEquals(asset,retrieved);
	}
	
	
	@Test
	public void processAsset() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		AssetProcessor<CSVAsset> csvProcessor = new AssetProcessor<CSVAsset>() {
			
			@Override
			public void process(CSVAsset asset) {
				latch.countDown();
			}
		};
		
		Processors.add(CSV,csvProcessor);
		
		Asset asset = new CSVAsset("1","test-asset",new TestRepository());
		
		Processors.process(asset);
		
		latch.await();
	
	}

}
