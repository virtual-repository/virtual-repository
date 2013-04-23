package org.fao.virtualrepository;

import org.fao.virtualrepository.spi.Repository;

/**
 * A data asset held in a concrete {@link Repository}, the so-called <em>origin</code>.
 * 
 * @author Fabio Simeoni
 * 
 * @see VirtualRepository
 * @see Repository
 */
public interface Asset {

	/**
	 * Returns the identifier of this asset.
	 * <p>
	 * The identifier must unambiguously distinguish this asset from any other asset within the origin or any other
	 * repository.
	 * 
	 * @return the identifier
	 */
	String id();

	/**
	 * Returns the name of this asset.
	 * <p>
	 * The name <em>should</code> unambiguously distinguish this asset from any other asset within the origin.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Returns the {@link AssetType} of this asset.
	 * 
	 * @return the type
	 */
	AssetType<?> type();

	/**
	 * Returns the origin of this asset.
	 * 
	 * @return the origin
	 */
	Repository origin();

	/**
	 * Returns the data stream of this asset under a given API.
	 * 
	 * @param api the type of the API
	 * @return the data stream
	 * 
	 * @throws IllegalStateException if the data stream cannot be returned under the given API
	 */
	<T> T data(Class<T> api);
}
