package org.fao.virtualrepository;

public interface Asset {

	String id();
	
	String name();
	
	AssetType<?> type();
}
