package org.fao.virtualrepository;

import org.fao.virtualrepository.spi.RepositoryDescription;

public interface Asset {

	String id();
	
	String name();
	
	AssetType<?> type();
	
	RepositoryDescription repository();
}
