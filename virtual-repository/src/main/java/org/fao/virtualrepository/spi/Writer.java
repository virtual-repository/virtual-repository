package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.AssetType;


public interface Writer {

	AssetType<?> type();
	
	//@TODO
}
