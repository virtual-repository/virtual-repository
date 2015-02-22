package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.RepositoryService;

/**
 * Extends {@link Asset} to allow internal management of assets discovered by {@link Browser}s.
 * <p>
 * Dispenses {@link Browser}s from keeping track of the associated {@link RepositoryService}.
 * 
 * @author Fabio Simeoni
 * @see Browser
 */
public interface MutableAsset extends Asset {

	/**
	 * Sets the {@link RepositoryService} with which this asset was retrieved or should be published
	 * @param service the service
	 */
	void setService(RepositoryService service);
}
