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

	
	//'name' is used in public static constants in subclasses. we alias the field to avoid shadowing problems
	// with certain tools that analyse the hierarchy reflectively (e.g. xstream).
	private final String _name;
	
	/**
	 * Creates an instance with a given _name.
	 * @param _name the _name
	 */
	public AbstractType(String name) {
		this._name=name;
	}
	
	@Override
	public String name() {
		return _name;
	};

	@Override
	public String toString() {
		return _name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	};
	
	
	
}
