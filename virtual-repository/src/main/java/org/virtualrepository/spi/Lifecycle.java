package org.virtualrepository.spi;




/**
 * Implemented by {@link VirtualProxy}s that require notifications of lifecycle events.
 *  
 * @author Fabio Simeoni
 *
 */
public interface Lifecycle {

	/**
	 * Invoked when the proxy is activated so that it can initialise.
	 * 
	 * @throws Exception if the proxy cannot be initialised
	 */
	void init() throws Exception;
}
