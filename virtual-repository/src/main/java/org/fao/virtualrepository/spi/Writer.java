package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;


public interface Writer<T extends Asset,A> {

	AssetType<T> type();
	
	Class<A> api();
	
	//@TODO
}
