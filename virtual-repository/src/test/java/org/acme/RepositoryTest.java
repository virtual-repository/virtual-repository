package org.acme;

import static java.util.Collections.*;
import static org.acme.TestUtils.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.csv.CSVAsset;
import org.fao.virtualrepository.impl.Repositories;
import org.fao.virtualrepository.impl.VirtualRepositoryImpl;
import org.fao.virtualrepository.processor.AssetProcessor;
import org.fao.virtualrepository.processor.Processors;
import org.junit.Test;

public class RepositoryTest {

	static CSV csvs  = new CSV();
	
	@Test
	public void ingest() {
		
		
		
		TestRepository repo = new TestRepository("test",new CSVAsset("1", "asset-1"));
		
		Repositories repos = new Repositories();
		
		repos.add(repo);
	
		VirtualRepository repository = new VirtualRepositoryImpl(repos);
		
		repository.ingest(csvs);
		
		Iterable<CSVAsset> assets = repository.get(csvs);
		
		assertEqualElements(singleton(new CSVAsset("1", "asset-1")),assets);
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
		
		Processors.add(csvs,csvProcessor);
		
		Asset asset = new CSVAsset("1", "test-asset");
		
		Processors.process(asset);
		
		latch.await(1,TimeUnit.SECONDS);
	
	}

}
