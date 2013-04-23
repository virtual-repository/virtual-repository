package org.acme;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.impl.Repositories;
import org.fao.virtualrepository.impl.VirtualRepositoryImpl;
import org.fao.virtualrepository.processor.AssetProcessor;
import org.fao.virtualrepository.processor.Processors;
import org.junit.Test;

public class VirtualRepositoryTest {

	@Test
	public void ingest() {
		
		TestRepository repo = new TestRepository("test");
		
		CSV asset = new CSV("1", "asset-1",repo); 
		
		repo.addAssets(asset);
		
		Repositories repos = new Repositories();
		
		repos.add(repo);
	
		VirtualRepository repository = new VirtualRepositoryImpl(repos);
		
		repository.ingest(CSV.type);
		
		Asset retrieved = repository.get("1");
		
		assertEquals(asset,retrieved);
	}
	
	
	@Test
	public void processAsset() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		AssetProcessor<CSV> csvProcessor = new AssetProcessor<CSV>() {
			
			@Override
			public void process(CSV asset) {
				latch.countDown();
			}
		};
		
		Processors.add(CSV.type,csvProcessor);
		
		Asset asset = new CSV("1","test-asset",new TestRepository());
		
		Processors.process(asset);
		
		latch.await();
	
	}

}
