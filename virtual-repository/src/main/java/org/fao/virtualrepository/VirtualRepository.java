package org.fao.virtualrepository;

import org.fao.virtualrepository.impl.Repositories;


public interface VirtualRepository {

	
	Repositories repositories();
	
	void ingest(AssetType<?> ... types);
	
	Iterable<Asset> get();
	
	<A extends Asset> Iterable<A> get(AssetType<A> type);
	
	Asset get(String id);
}
