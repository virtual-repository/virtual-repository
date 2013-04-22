package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

public interface Reader<T> {


	Iterable<? extends Asset> find();
	
	T fetch(Asset asset);
	
	AssetType<?> type();
	
	Class<T> api();
	
	//@TODO
}
