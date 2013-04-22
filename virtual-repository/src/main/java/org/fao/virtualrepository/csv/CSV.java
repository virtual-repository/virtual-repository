package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.AssetType;

public final class CSV implements AssetType<CSVAsset> {
	
	@Override
	public CSVFormat format() {
		return CSVFormat.format;
	}
	
	@Override
	public String toString() {
		return CSVFormat.format.name();
	}
}
