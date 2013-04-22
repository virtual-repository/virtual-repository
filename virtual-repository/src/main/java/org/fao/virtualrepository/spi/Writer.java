package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.AssetType;


public interface Writer<T> {

	AssetType<?> type();
	
	Class<T> api();
	
	//@TODO
}
