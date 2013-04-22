package org.fao.virtualrepository;

import org.fao.virtualrepository.spi.Repository;

public interface Asset {

	String id();
	
	String name();
	
	AssetType<?> type();
	
	Repository origin();
}
