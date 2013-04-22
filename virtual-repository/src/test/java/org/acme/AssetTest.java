package org.acme;


import static org.junit.Assert.*;

import org.fao.virtualrepository.csv.CSVAsset;
import org.junit.Test;

public class AssetTest {

	@Test
	public void readAsset() {
		
		String content = "foo";
		
		TestRepository repo = new TestRepository();
		
		repo.setReader(repo.new TestReader(content));
		
		CSVAsset asset = new CSVAsset("1","test",repo);
		
		String data = asset.data(String.class);
		
		assertEquals(content, data);
	}
	
		
}
