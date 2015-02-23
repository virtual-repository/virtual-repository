package org.virtualrepository.spi;

import java.util.List;

import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;

/**
 * A proxy to a {@link Repository}.
 * 
 * <p>
 * The proxy provides objects that know how to access the service, including a {@link VirtualBrowser}, one ore more {@link VirtualReader}s and/or one or more {@link VirtualWriter}s.
 * 
 * @author Fabio Simeoni
 * 
 * @see VirtualRepository
 */
public interface VirtualProxy {

	/**
	 * Returns the {@link VirtualBrowser} bound to this repository service.
	 * @return
	 */
	VirtualBrowser browser();

	/**
	 * Returns the {@link VirtualReader}s bound to this repository service, if any.
	 * 
	 * @return the importers
	 */
	List<? extends VirtualReader<?,?>> importers();

	/**
	 * Returns the {@link VirtualWriter}s bound to this repository service, if any.
	 * 
	 * @return the publishers
	 */
	List<? extends VirtualWriter<?,?>> publishers();

}
