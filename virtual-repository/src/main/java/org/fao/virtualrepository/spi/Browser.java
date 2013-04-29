package org.fao.virtualrepository.spi;

import java.util.List;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * Discovers data assets available through a {@link RepositoryService}, the <em>bound service</em>.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Browser {

	/**
	 * Returns all the assets of given types which are available through the bound service.
	 * 
	 * @param the asset types
	 * @return the assets
	 * 
	 * @throws Exception if the assets cannot be discovered
	 */
	Iterable<? extends Asset> discover(List<AssetType<?>> assets) throws Exception;
}
