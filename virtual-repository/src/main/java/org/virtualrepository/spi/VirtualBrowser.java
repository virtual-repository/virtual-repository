package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;

/**
 * Discovers data assets available through a {@link Repository}, the <em>bound service</em>.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface VirtualBrowser {

	/**
	 * Returns all the assets of given types which are available through the bound service.
	 * <p>
	 * This method is invoked only if the {@link Repository} declares an {@link VirtualReader} for at least one of the
	 * given types. Implementations do not need to perform this check, and may in fact ignore the input entirely if
	 * their {@link Repository} supports only one type.
	 * 
	 * @param types the asset types
	 * @return the assets
	 * 
	 * @throws Exception if the assets cannot be discovered
	 */
	Iterable<? extends Asset.Private> discover(Collection<? extends AssetType> types) throws Exception;
}
