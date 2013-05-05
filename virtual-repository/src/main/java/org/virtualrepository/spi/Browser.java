package org.virtualrepository.spi;

import java.util.List;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Discovers data assets available through a {@link RepositoryService}, the <em>bound service</em>.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Browser {

	/**
	 * Returns all the assets of given types which are available through the bound service.
	 * <p>
	 * This method is invoked only if the {@link RepositoryService} declares an {@link Importer} for at least one of the
	 * given types. Implementations do not need to perform this check, and may in fact ignore the input entirely if
	 * their {@link RepositoryService} supports only one type.
	 * 
	 * @param types the asset types
	 * @return the assets
	 * 
	 * @throws Exception if the assets cannot be discovered
	 */
	Iterable<? extends Asset> discover(List<? extends AssetType> types) throws Exception;
}
