package org.virtualrepository.spi;

import java.util.List;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.VirtualRepository;

/**
 * A proxy to a {@link RepositoryService}.
 * 
 * <p>
 * The proxy provides objects that know how to access the service, including a {@link Browser}, one ore more {@link Importer}s and/or one or more {@link Publisher}s.
 * 
 * @author Fabio Simeoni
 * 
 * @see VirtualRepository
 */
public interface ServiceProxy {

	/**
	 * Returns the {@link Browser} bound to this repository service.
	 * @return
	 */
	Browser browser();

	/**
	 * Returns the {@link Importer}s bound to this repository service, if any.
	 * 
	 * @return the importers
	 */
	List<? extends Importer<?,?>> importers();

	/**
	 * Returns the {@link Publisher}s bound to this repository service, if any.
	 * 
	 * @return the publishers
	 */
	List<? extends Publisher<?,?>> publishers();

}
