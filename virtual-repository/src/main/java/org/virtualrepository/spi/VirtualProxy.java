package org.virtualrepository.spi;

import java.util.List;

/**
 * A proxy to a given repository.
 * <p>
 * Provides an asset browser and one or more asset readers and/or asset writers.
 */
public interface VirtualProxy extends Lifecycle {

	/**
	 * The repository browser.
	 */
	VirtualBrowser browser();

	/**
	*  The repository readers.
	*/
	List<VirtualReader<?>> readers();
	
	
	/**
	 * The repository writers.
	 */
	List<VirtualWriter<?>> writers();

}
