package org.fao.virtualrepository.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.Writer;

public class RepositoryManager {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Reader<?>>> readers = new HashMap<Class<? extends AssetType>, Set<Reader<?>>>();

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Writer<?>>> writers = new HashMap<Class<? extends AssetType>, Set<Writer<?>>>();

	public RepositoryManager(Repository repository) {

		for (Reader<?> reader : repository.readers())
			addReader(reader);

		for (Writer<?> writer : repository.writers())
			addWriter(writer);

	}

	public Map<String,Asset> ingest(AssetType<?>... types) {

		Map<String,Asset> assets = new HashMap<String, Asset>();
		
		for (AssetType<?> type : types)
			for (Reader<?> reader : readers(type))
				for (Asset asset : reader.find())
					assets.put(asset.id(),asset);

		return assets;
	}

	// helper
	private void addReader(Reader<?> reader) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = reader.type().getClass();

		if (!readers.containsKey(typeClass)) {
			readers.put(typeClass, new HashSet<Reader<?>>());
		}

		readers.get(typeClass).add(reader);
	}

	// helper
	private void addWriter(Writer<?> writer) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = writer.type().getClass();

		if (!writers.containsKey(typeClass)) {
			writers.put(typeClass, new HashSet<Writer<?>>());
		}

		writers.get(typeClass).add(writer);
	}

	public <T> Reader<T> reader(AssetType<?> type, Class<T> api) {

		for (Reader<?> reader : readers(type))
			if (api.isAssignableFrom(reader.api())) {
				
				@SuppressWarnings("unchecked")
				Reader<T> typed = (Reader<T>) reader;
				
				return typed;
			}
		
		throw new IllegalStateException("no reader available for type "+type+" with API "+api);
	}
	
	public Set<? extends Reader<?>> readers(AssetType<?> type) {

		return readers.containsKey(type.getClass()) ? 
					readers.get(type.getClass()) : 
					Collections.<Reader<?>> emptySet();

	}

	public Set<Writer<?>> writers(AssetType<?> type) {

		return writers.containsKey(type.getClass()) ? 
					writers.get(type.getClass()) : 
					Collections.<Writer<?>> emptySet();

	}
}
