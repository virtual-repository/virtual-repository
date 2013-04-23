package org.fao.virtualrepository.impl;

import static java.util.Arrays.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualRepositoryImpl implements VirtualRepository {
	
	private final static Logger log = LoggerFactory.getLogger(VirtualRepository.class);
	
	private final Repositories repositories;
	
	private Map<String,Asset> assets = new HashMap<String, Asset>();
	
	public VirtualRepositoryImpl() {

		repositories = new Repositories();
		repositories.load();
		 
	}
	
	public VirtualRepositoryImpl(Repositories repositories) {
		 this.repositories = repositories;
	}
	
	@Override
	public Repositories repositories() {
		return repositories; 
	}
	
	@Override
	public void ingest(AssetType<?>... types) {
		
		log.info("ingesting resources of types ({})",asList(types));
		
		for (Repository repository : repositories) {

			RepositoryManager manager = new RepositoryManager(repository); 
			
			for (AssetType<?> type : types)
				for (Reader<?, ?> reader : manager.readers(type))
					for (Asset asset : reader.find())
						assets.put(asset.id(), asset);
		}
	}
	
	@Override
	public Iterator<Asset> iterator() {
		return assets.values().iterator();
	}
	
	@Override
	public Asset get(String id) {
		
		Asset asset = assets.get(id);
		
		if (asset==null)
			throw new IllegalStateException("unknown asset "+id);
		else
			return asset;
		
	}
	
}
