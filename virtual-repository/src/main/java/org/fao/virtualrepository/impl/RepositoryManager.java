package org.fao.virtualrepository.impl;

import static org.fao.virtualrepository.Utils.*;

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

/**
 * Used internally to wrap a {@link Repository} and simplify access to its {@link Reader}s and {@link Writer}s.
 * 
 * @author Fabio Simeoni
 */
public class RepositoryManager {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Reader<?, ?>>> readers = new HashMap<Class<? extends AssetType>, Set<Reader<?, ?>>>();

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Writer<?,?>>> writers = new HashMap<Class<? extends AssetType>, Set<Writer<?,?>>>();

	/**
	 * Creates an instance for a given {@link Repository}
	 * 
	 * @param repository the repository
	 */
	public RepositoryManager(Repository repository) {

		notNull("repository",repository);
		
		for (Reader<?, ?> reader : repository.readers())
			addReader(reader);

		for (Writer<?,?> writer : repository.writers())
			addWriter(writer);

	}

	/**
	 * Returns a {@link Reader} of the managed {@link Repository} bound to a given {@link AssetType} and API.
	 * @param type the bound type of the reader
	 * @param api the bound API of the reader
	 * @return the reader
	 * 
	 * @throws IllegalStateException if the managed {@link Repository} has no reader bound to the given type and API
	 */
	public <A,T extends Asset> Reader<T, A> reader(AssetType<T> type, Class<A> api) {

		notNull(type);
		
		for (Reader<?, ?> reader : readers(type))
			if (api.isAssignableFrom(reader.api())) {

				@SuppressWarnings("unchecked")
				Reader<T, A> typed = (Reader<T, A>) reader;

				return typed;
			}

		throw new IllegalStateException("no reader available for type " + type + " with API " + api);
	}

	/**
	 * Returns all the {@link Reader}s of the managed {@link Repository} that are bound to a given {@link AssetType}.
	 * @param type the bound type of the readers
	 * 
	 * @return the readers
	 * 
	 */
	public Set<? extends Reader<?, ?>> readers(AssetType<?> type) {

		notNull(type);
		
		return readers.containsKey(type.getClass()) ? readers.get(type.getClass()) : Collections
				.<Reader<?, ?>> emptySet();

	}

	/**
	 * Returns all the {@link Writer}s of the managed {@link Repository} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the readers
	 * 
	 * @return the writers
	 * 
	 */
	public Set<Writer<?,?>> writers(AssetType<?> type) {

		notNull(type);
		
		return writers.containsKey(type.getClass()) ? writers.get(type.getClass()) : Collections.<Writer<?,?>> emptySet();

	}
	
	
	// helper
	private void addReader(Reader<?, ?> reader) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = reader.type().getClass();

		if (!readers.containsKey(typeClass)) {
			readers.put(typeClass, new HashSet<Reader<?, ?>>());
		}

		readers.get(typeClass).add(reader);
	}

	// helper
	private void addWriter(Writer<?,?> writer) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = writer.type().getClass();

		if (!writers.containsKey(typeClass)) {
			writers.put(typeClass, new HashSet<Writer<?,?>>());
		}

		writers.get(typeClass).add(writer);
	}

}
