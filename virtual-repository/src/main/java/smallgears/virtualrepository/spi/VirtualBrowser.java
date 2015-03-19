package smallgears.virtualrepository.spi;

import java.util.Collection;

import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;

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
	Collection<Asset> discover(Collection<AssetType> types) throws Exception;
}
