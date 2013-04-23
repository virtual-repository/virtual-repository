package org.fao.virtualrepository;

import org.fao.virtualrepository.csv.CSVAsset;

public interface AssetType<A extends Asset> {

	public static AssetType<CSVAsset> CSV = new AssetType<CSVAsset>() {
		
		public static final String NAME = "CSV";
		
		
		public String name() {
			return NAME;
		};
	};
	
	String name();
	
	
}
