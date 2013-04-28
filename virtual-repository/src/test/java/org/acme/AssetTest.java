package org.acme;


import static org.junit.Assert.*;

import org.fao.virtualrepository.Asset;
import org.junit.Test;

public class AssetTest {

	@Test
	public void assetHandlesMissingRightReader() {
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().with(10).add();
		
		//no reader for integers
		try {
			asset.data(Integer.class);
			fail();
		}
		catch(IllegalStateException e) {}
		
	}
	
	@Test
	public void assetDispatchesToRightReader() {
		
		final int mockRemoteData = 10;
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().with(mockRemoteData).add();
		
		//add reader for integers
		repo.addReader().yields(Integer.class);
		
		int imported = asset.data(Integer.class);
		
		assertEquals(mockRemoteData, imported);
	}
	
	
		
}
