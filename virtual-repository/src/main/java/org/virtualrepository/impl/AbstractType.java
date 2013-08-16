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
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractType<?> other = (AbstractType<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	};
	
	
	
}
