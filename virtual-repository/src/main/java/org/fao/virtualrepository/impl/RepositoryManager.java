package org.fao.virtualrepository.impl;

import static org.fao.virtualrepository.Utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.spi.Importer;
import org.fao.virtualrepository.spi.Publisher;
import org.fao.virtualrepository.spi.RepositoryService;

/**
 * Used internally to wrap a {@link RepositoryService} and simplify access to its {@link Importer}s and
 * {@link Publisher}s.
 * 
 * @author Fabio Simeoni
 */
class RepositoryManager {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Importer<?, ?>>> importers = new HashMap<Class<? extends AssetType>, Set<Importer<?, ?>>>();

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>, Set<Publisher<?, ?>>> publishers = new HashMap<Class<? extends AssetType>, Set<Publisher<?, ?>>>();

	/**
	 * Creates an instance for a given {@link RepositoryService}
	 * 
	 * @param repository the repository
	 */
	public RepositoryManager(RepositoryService repository) {

		notNull("repository", repository);

		for (Importer<?, ?> reader : repository.importers())
			addImporter(reader);

		for (Publisher<?, ?> writer : repository.publishers())
			addPublisher(writer);

	}

	/**
	 * Returns the {@link AssetType}s that, among a given set of such types, are supported by the
	 * {@link RepositoryService}.
	 * 
	 * @param types the given set of types
	 * @return the supported types
	 */
	public List<AssetType<?>> supports(AssetType<?>... types) {

		List<AssetType<?>> supported = new ArrayList<AssetType<?>>();

		for (AssetType<?> type : types)
			if (!readers(type).isEmpty())
				supported.add(type);

		return supported;
	}

	/**
	 * Returns an {@link Importer} of the {@link RepositoryService} which is bound to the {@link AssetType} of a given
	 * {@link Asset} and to a given API.
	 * 
	 * @param asset the asset
	 * @param api the bound API
	 * @return the importer
	 * 
	 * @throws IllegalStateException if the {@link RepositoryService} has no importer bound to the type of the given
	 *             asset and the given API
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
	 * Returns all the {@link Importer}s of the {@link RepositoryService} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the importers
	 * 
	 * @return the importers
	 * 
	 */
	public Set<? extends Importer<?, ?>> readers(AssetType<?> type) {

		notNull(type);

		return importers.containsKey(type.getClass()) ? importers.get(type.getClass()) : Collections
				.<Importer<?, ?>> emptySet();

	}

	/**
	 * Returns all the {@link Publisher}s of the {@link RepositoryService} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the publishers
	 * 
	 * @return the publishers
	 * 
	 */
	public Set<? extends Publisher<?, ?>> writers(AssetType<?> type) {

		notNull(type);

		return publishers.containsKey(type.getClass()) ? publishers.get(type.getClass()) : Collections
				.<Publisher<?, ?>> emptySet();

	}

	/**
	 * Returns a {@link Publisher} of the {@link RepositoryService} bound to the {@link AssetType} of a given
	 * {@link Asset} and to a given API.
	 * 
	 * @param asset the asset
	 * @param api the bound API of the publisher
	 * @return the publisher
	 * 
	 * @throws IllegalStateException if the {@link RepositoryService} has no publisher bound to the type of the given
	 *             asset and the given API
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
	private void addImporter(Importer<?, ?> importer) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = importer.type().getClass();

		if (!importers.containsKey(typeClass)) {
			importers.put(typeClass, new HashSet<Importer<?, ?>>());
		}

		importers.get(typeClass).add(importer);
	}

	// helper
	private void addPublisher(Publisher<?, ?> publisher) {

		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = publisher.type().getClass();

		if (!publishers.containsKey(typeClass)) {
			publishers.put(typeClass, new HashSet<Publisher<?, ?>>());
		}

		publishers.get(typeClass).add(publisher);
	}

}
