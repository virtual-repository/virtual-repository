package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Discovers assets in a target repository.
 */
public interface VirtualBrowser {

	/**
	 * All the assets of given types available through the target repository.
	 * <p>
	 * Invoked only if the target repository has a reader for at least one of the
	 * given types. Implementations may ignore the input entirely if
	 * the target repository supports only one type.
	 */
	Iterable<Asset> discover(Collection<? extends AssetType> types) throws Exception;
}
