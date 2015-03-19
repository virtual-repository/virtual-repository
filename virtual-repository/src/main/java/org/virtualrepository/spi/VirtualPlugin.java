package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.Repository;


/**
 * A plugin's entry point.
 * <p>
 * Provides access to the repositories connected by the plugin.
 *
 */
public interface VirtualPlugin extends Lifecycle {

	
	/**
	 * The repositories connected by the plugin.
	 */
	Collection<Repository> repositories();
	
	
	
	/**
	 * Invoked when the proxy or plugin are de-activated.
	 */
	default void shutdown() throws Exception {}
}
