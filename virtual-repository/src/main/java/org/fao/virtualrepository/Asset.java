package org.fao.virtualrepository;

import org.fao.virtualrepository.spi.RepositoryService;

/**
 * A data asset held in or destined for a {@link RepositoryService}.
 * 
 * @author Fabio Simeoni
 * 
 * @see VirtualRepository
 * @see RepositoryService
 */
public interface Asset {

	/**
	 * Returns the identifier of this asset.
	 * <p>
	 * The identifier must unambiguously distinguish this asset from any other asset available through any repository service.
	 * 
	 * @return the identifier
	 */
	String id();

	/**
	 * Returns the name of this asset.
	 * <p>
	 * The name <em>should</code> unambiguously distinguish this asset from any other asset available through the associated repository service.
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
	 * Returns the repository service from which this asset originates or should be published to.
	 * 
	 * @return the origin
	 */
	RepositoryService repository();
	
	/**
	 * Returns the properties of this asset, if any.
	 * @return the properties
	 */
	Properties properties();
}
