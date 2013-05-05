package org.virtualrepository.impl;

import org.virtualrepository.Asset;

/**
 * Partial implementation of {@link Type}.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the (language) type of {@link Asset}s of this type
 */
public abstract class AbstractType<T extends Asset> implements Type<T>  {

	
	private final String name;
	
	/**
	 * Creates an instance with a given name.
	 * @param name the name
	 */
	public AbstractType(String name) {
		this.name=name;
	}
	
	@Override
	public String name() {
		return name;
	};

	@Override
	public String toString() {
		return name;
	};
}
