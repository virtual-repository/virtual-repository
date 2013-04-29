package org.fao.virtualrepository.impl;

import static org.fao.virtualrepository.Utils.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.spi.Importer;
import org.fao.virtualrepository.spi.RepositoryService;
import org.fao.virtualrepository.spi.Publisher;

/**
 * Used internally to wrap a {@link RepositoryService} and simplify access to its {@link Importer}s and {@link Publisher}s.
 * 
 * @author Fabio Simeoni
 */
class RepositoryManager {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Importer<?, ?>>> readers = new HashMap<Class<? extends AssetType>, Set<Importer<?, ?>>>();

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Publisher<?, ?>>> writers = new HashMap<Class<? extends AssetType>, Set<Publisher<?, ?>>>();

	/**
	 * Creates an instance for a given {@link RepositoryService}
	 * 
	 * @param repository the repository
	 */
	public RepositoryManager(RepositoryService repository) {

		notNull("repository", repository);

		for (Importer<?, ?> reader : repository.importers())
			addReader(reader);

		for (Publisher<?, ?> writer : repository.publishers())
			addWriter(writer);

	}

	/**
	 * Returns a {@link Importer} of the managed {@link RepositoryService} bound to the type of a given asset {@link AssetType}
	 * and to a given API.
	 * 
	 * @param asset the asset with the bound type of the reader
	 * @param api the bound API of the reader
	 * @return the reader
	 * 
	 * @throws IllegalStateException if the managed {@link RepositoryService} has no reader bound to the type of the given asset and the given API
	 */
	public <A, T extends Asset> Importer<T, A> reader(T asset, Class<? extends A> api) {

		notNull(asset);

		for (Importer<?, ?> reader : readers(asset.type()))
			if (api.isAssignableFrom(reader.api())) {

				@SuppressWarnings("unchecked")
				Importer<T, A> typed = (Importer<T, A>) reader;

				return typed;
			}

		throw new IllegalStateException("no reader available for type " + asset.type() + " with API " + api);
	}

	/**
	 * Returns all the {@link Importer}s of the managed {@link RepositoryService} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the readers
	 * 
	 * @return the readers
	 * 
	 */
	public Set<? extends Importer<?, ?>> readers(AssetType<?> type) {

		notNull(type);

		return readers.containsKey(type.getClass()) ? readers.get(type.getClass()) : Collections
				.<Importer<?, ?>> emptySet();

	}

	/**
	 * Returns all the {@link Publisher}s of the managed {@link RepositoryService} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the readers
	 * 
	 * @return the writers
	 * 
	 */
	public Set<? extends Publisher<?, ?>> writers(AssetType<?> type) {

		notNull(type);

		return writers.containsKey(type.getClass()) ? writers.get(type.getClass()) : Collections
				.<Publisher<?, ?>> emptySet();

	}

	/**
	 * Returns a {@link Publisher} of the managed {@link RepositoryService} bound to the type of a given {@link Asset} and to a
	 * given API.
	 * 
	 * @param asset the asset with the bound type of the writer
	 * @param api the bound API of the writer
	 * @return the writer
	 * 
	 * @throws IllegalStateException if the managed {@link RepositoryService} has no writer bound to the type of the given
	 *             asset to the given API
	 */
	public <A, T extends Asset> Publisher<T, A> writer(T asset, Class<? extends A> api) {

		notNull(asset);
		notNull(api);

		for (Publisher<?, ?> writer : writers(asset.type()))
			
			if (writer.api().isAssignableFrom(api)) {

				@SuppressWarnings("unchecked")
				Publisher<T, A> typed = (Publisher<T, A>) writer;

				return typed;
			}

		throw new IllegalStateException("no reader available for type " + asset.type() + " with API " + api);
	}

	// helper
	private void addReader(Importer<?, ?> reader) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = reader.type().getClass();

		if (!readers.containsKey(typeClass)) {
			readers.put(typeClass, new HashSet<Importer<?, ?>>());
		}

		readers.get(typeClass).add(reader);
	}

	// helper
	private void addWriter(Publisher<?, ?> writer) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = writer.type().getClass();

		if (!writers.containsKey(typeClass)) {
			writers.put(typeClass, new HashSet<Publisher<?, ?>>());
		}

		writers.get(typeClass).add(writer);
	}

}
