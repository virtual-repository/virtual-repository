package org.virtualrepository.impl;

import static org.virtualrepository.common.Utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.Types;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;
import org.virtualrepository.spi.VirtualProxy;

public class ServiceInspector {

	private final VirtualProxy proxy;
	
	public ServiceInspector(Repository service) {
		
		this.proxy = service.proxy();
		
	}

	/**
	 * Creates an instance for a given {@link RepositoryService}
	 * 
	 * @param service the service
	 */


	/**
	 * Returns the {@link AssetType}s supported for import by the {@link Repository} among a given set of
	 * such types.
	 * 
	 * @param types the given set of types
	 * @return the supported types
	 */
	public List<AssetType> returned(AssetType... types) {

		notNull("asset types", types);

		Set<AssetType> supported = new HashSet<AssetType>();

		for (AssetType type : types)
			if (!importersFor(type).isEmpty())
				supported.add(type);
		
		return new ArrayList<AssetType>(supported);
	}

	/**
	 * Returns the {@link AssetType}s supported for publication by the {@link Repository}.
	 * 
	 * @param types the given set of types
	 * @return the supported types
	 */
	public List<AssetType> taken(AssetType... types) {

		notNull("asset types", types);

		Set<AssetType> supported = new HashSet<AssetType>();

		for (AssetType type : types)
			if (!publishersFor(type).isEmpty())
				supported.add(type);
				
		return new ArrayList<AssetType>(supported);
	}


	/**
	 * Returns an {@link VirtualReader} of the {@link Repository} which is bound to the {@link AssetType} of a given
	 * {@link Asset} and to a given API.
	 * 
	 * @param asset the asset
	 * @param api the bound API
	 * @return the importer
	 * 
	 * @throws IllegalStateException if the {@link Repository} has no importer bound to the type of the given
	 *             asset and the given API
	 */
	public <A, T extends Asset> VirtualReader<T, A> importerFor(AssetType type, Class<? extends A> api) {

		notNull("asset type", type);

		for (VirtualReader<?, ?> reader : importersFor(type))
			if (api.isAssignableFrom(reader.api())) {

				@SuppressWarnings("unchecked")
				VirtualReader<T, A> typed = (VirtualReader<T, A>) reader;

				return typed;
			}

		throw new IllegalStateException("no importer available for type " + type + " with API " + api);
	}

	/**
	 * Returns all the {@link VirtualReader}s of the {@link Repository} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the importers
	 * 
	 * @return the importers
	 * 
	 */
	public Set<? extends VirtualReader<?, ?>> importersFor(AssetType type) {

		notNull(type);
		
		Set<VirtualReader<?,?>> importers = new HashSet<VirtualReader<?,?>>();
		
		for (VirtualReader<?,?> importer : proxy.importers())
			if (importer.type()==Types.any || importer.type().equals(type))
				importers.add(importer);
		
		return importers;

	}

	/**
	 * Returns all the {@link VirtualWriter}s of the {@link Repository} that are bound to a given {@link AssetType}.
	 * 
	 * @param type the bound type of the publishers
	 * 
	 * @return the publishers
	 * 
	 */
	public Set<? extends VirtualWriter<?, ?>> publishersFor(AssetType type) {

		notNull(type);
		
		Set<VirtualWriter<?,?>> publishers = new HashSet<VirtualWriter<?,?>>();
		for (VirtualWriter<?,?> publisher : proxy.publishers())
			if (publisher.type()==Types.any || publisher.type().equals(type))
				publishers.add(publisher);

		return publishers;

	}

	/**
	 * Returns a {@link VirtualWriter} of the {@link Repository} bound to the {@link AssetType} of a given
	 * {@link Asset} and to a given API.
	 * 
	 * @param asset the asset
	 * @param api the bound API of the publisher
	 * @return the publisher
	 * 
	 * @throws IllegalStateException if the {@link Repository} has no publisher bound to the type of the given
	 *             asset and the given API
	 */
	public <A, T extends Asset> VirtualWriter<T, A> publisherFor(AssetType type, Class<? extends A> api) {

		notNull("asset type", type);
		notNull(api);

		for (VirtualWriter<?, ?> writer : publishersFor(type))

			if (writer.api().isAssignableFrom(api)) {

				@SuppressWarnings("unchecked")
				VirtualWriter<T, A> typed = (VirtualWriter<T, A>) writer;

				return typed;
			}

		throw new IllegalStateException("no publisher available for type " + type + " with API " + api);
	}

	/**
	 * Returns <code>true</code> if the service can retrieve assets of a given {@link AssetType} with a given API.
	 * 
	 * @param type the type
	 * @param api the api
	 * @return <code>true</code> if the service can retrieve assets of the given type with the given API
	 */
	public boolean returns(AssetType type, Class<?> api) {
		try {
			importerFor(type, api);
			return true;
		} catch (IllegalStateException e) {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> if the service can publish assets of a given {@link AssetType} with a given API.
	 * 
	 * @param type the type
	 * @param api the api
	 * @return <code>true</code> if the service can publish assets of the given type with the given API
	 */
	public boolean takes(AssetType type, Class<?> api) {
		try {
			publisherFor(type, api);
			return true;
		} catch (IllegalStateException e) {
			return false;
		}
	}

}
