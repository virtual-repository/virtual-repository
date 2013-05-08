package org.virtualrepository.spi;

import java.util.Collection;


/**
 * The entry point of a library plugin.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Plugin {

	/**
	 * Returns the {@link ServiceProxy}s exported by this plugin.
	 * @return the services
	 */
	Collection<RepositoryService> services();
}
