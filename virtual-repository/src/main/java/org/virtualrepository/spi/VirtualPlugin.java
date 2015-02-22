package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.RepositoryService;


/**
 * The entry point of a plugin library.
 * 
 * @author Fabio Simeoni
 *
 */
public interface VirtualPlugin {

	/**
	 * Returns the services exported by this plugin.
	 * @return the services
	 */
	Collection<RepositoryService> services();
}
