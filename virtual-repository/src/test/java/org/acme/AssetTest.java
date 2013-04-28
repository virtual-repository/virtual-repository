package org.acme;


import static org.junit.Assert.*;

import org.fao.virtualrepository.Asset;
import org.junit.Test;

public class AssetTest {

	@Test
	public void assetHandlesMissingReader() {
		
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
		
		final int data = 10;
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().with(data).add();
		
		//add reader for integers
		repo.addReader().yields(Integer.class);
		
		int imported = asset.data(Integer.class);
		
		assertEquals(data, imported);
	}
	
	@Test
	public void assetStoresLocalData() {
		
		final int data = 10;
		
		TestRepo repo = new TestRepo();
		
		//data in asset, reader is not required
		Asset asset = repo.asset().withLocal(data).add();
		
		int imported = asset.data(Integer.class);
		
		assertEquals(data, imported);
	}
	
	@Test
	public void assetHandlesWrongAPIoverLocalData() {
		
		final int data = 10;
		
		TestRepo repo = new TestRepo();
		
		Asset asset = repo.asset().withLocal(data).add();
		
		try {
			asset.data(String.class);
			fail();
		}
		catch(IllegalStateException e) {}
	}
	
	
		
}
