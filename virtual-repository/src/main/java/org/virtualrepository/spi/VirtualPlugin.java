package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.Repository;


/**
 * A plugin's entry point.
 * <p>
 * Provides access to the repositories connected by the plugin.
 *
 */
public interface VirtualPlugin {

	/**
	 * The repositories connected by the plugin.
	 */
	Collection<Repository> repositories();
}
