package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.Repository;

public class CSVAsset extends AbstractAsset {

	public CSVAsset(String id, String name, Repository origin) {
		super(id,name,origin);
	}
	
	@Override
	public CSV type() {
		return new CSV();
	}

	@Override
	public String toString() {
		return "CSVAsset [id=" + id() + ", name=" + name() + "]";
	}

	
}
