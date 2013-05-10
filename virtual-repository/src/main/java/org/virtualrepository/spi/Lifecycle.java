package org.virtualrepository.spi;

import org.virtualrepository.RepositoryService;



/**
 * The interface of {@link RepositoryService}s that require notifications of lifecycle events.
 *  
 * @author Fabio Simeoni
 *
 */
public interface Lifecycle {

	/**
	 * Invoked when the service is activated.
	 * 
	 * @throws Exception
	 */
	void init() throws Exception;
}
