package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

public interface Reader {


	Iterable<? extends Asset> get();
	
	AssetType<?> type();
	
	//@TODO
}
