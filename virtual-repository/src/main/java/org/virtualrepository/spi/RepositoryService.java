package org.virtualrepository.spi;

import java.util.List;

import javax.xml.namespace.QName;

import org.virtualrepository.Properties;
import org.virtualrepository.VirtualRepository;

/**
 * A repository service underlying a {@link VirtualRepository}.
 * <p>
 * A repository service is named, can discover assets, and has a number of {@link Importer}s and/or {@link Publisher}s to, respectively, retrieve and publish
 * data of assets available through the service.
 * 
 * @author Fabio Simeoni
 * @see VirtualRepository
 */
public interface RepositoryService {

	/**
	 * Returns the name of this repository service.
	 * <p>
	 * The name must distinguish the repository service from other services underlying the same {@link VirtualRepository}.
	 * 
	 * @return the name
	 */
	QName name();
	
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
	
	
	/**
	 * Returns the properties of this repository, if any.
	 * @return the properties
	 */
	Properties properties();
	

}
