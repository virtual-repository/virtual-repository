package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.Repository;

public class CSVAsset extends AbstractAsset<CSVAsset> {

	public CSVAsset(String id, String name, Repository origin) {
		super(id,name,origin);
	}
	
	@Override
	public AssetType<CSVAsset> type() {
		return AssetType.CSV;
	}
	
	@Override
	public String toString() {
		return "CSVAsset [id=" + id() + ", name=" + name() + "]";
	}

	
}
