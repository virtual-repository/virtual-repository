package org.virtualrepository.impl;

import org.virtualrepository.Properties;

/**
 * The interface of objects with {@link Properties}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Described {

	/**
	 * Returns the properties of this object.
	 * @return the properties
	 */
	Properties properties();
}
