package org.fao.virtualrepository.spi;

import java.util.List;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.Properties;
import org.fao.virtualrepository.VirtualRepository;

/**
 * The interface of a concrete repository underlying a {@link VirtualRepository}.
 * <p>
 * A repository is named and has a number of {@link Reader}s and/or {@link Writer}s to, respectively, retrieve and publish
 * data {@link Asset} streams from and to the repository.
 * 
 * @author Fabio Simeoni
 * @see VirtualRepository
 */
public interface Repository {

	/**
	 * Returns the name of this repository.
	 * <p>
	 * The name must distinguish the repository from others underlying the same {@link VirtualRepository}.
	 * 
	 * @return the name
	 */
	QName name();

	/**
	 * Returns the {@link Reader}s available for this repository, if any.
	 * 
	 * @return the readers
	 */
	List<? extends Reader<?,?>> readers();

	/**
	 * Returns the {@link Writer}s available for this repository, if any.
	 * 
	 * @return the writers
	 */
	List<? extends Writer<?,?>> writers();
	
	
	/**
	 * Returns the properties of this repository, if any.
	 * @return the properties
	 */
	Properties properties();
	

}
