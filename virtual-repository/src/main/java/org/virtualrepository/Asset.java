package org.virtualrepository;

import org.virtualrepository.spi.RepositoryService;


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
	 * The identifier must unambiguously distinguish this asset from any other asset that can be retrieved or published
	 * with any repository service.
	 * 
	 * @return the identifier
	 */
	String id();

	/**
	 * Returns the name of this asset.
	 * <p>
	 * The name must unambiguously distinguish this asset from any other asset that can be retrieved or
	 * published with repository service associated with this asset.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Returns the {@link AssetType} of this asset.
	 * 
	 * @return the type
	 */
	AssetType type();

	/**
	 * Returns the {@link RepositoryService} with which this asset was retrieved or should be published.
	 * 
	 * @return the origin
	 */
	RepositoryService service();

	/**
	 * Returns the {@link Properties} of this asset.
	 * 
	 * @return the properties
	 */
	Properties properties();
}
