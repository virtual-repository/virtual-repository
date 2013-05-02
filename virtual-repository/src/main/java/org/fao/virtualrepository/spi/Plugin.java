package org.fao.virtualrepository.spi;

import java.util.List;

/**
 * The entry point of a library plugin.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Plugin {

	/**
	 * Returns the {@link RepositoryService}s exported by this plugin.
	 * @return the services
	 */
	List<? extends RepositoryService> services();
}
