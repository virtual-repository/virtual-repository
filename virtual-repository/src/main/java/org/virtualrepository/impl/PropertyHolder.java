package org.virtualrepository.impl;

import org.virtualrepository.Properties;

/**
 * Base implementation of {@link Described}.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class PropertyHolder implements Described {

	private PropertyProvider provider = new PropertyProvider.Simple();
	
	@Override
	public Properties properties() {
		return provider.properties();
	}
	
	public void properties(PropertyProvider provider) {
		this.provider=provider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((provider.properties() == null) ? 0 : provider.properties().hashCode());
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
		PropertyHolder other = (PropertyHolder) obj;
		if (properties() == null) {
			if (other.properties() != null)
				return false;
		} else if (!properties().equals(other.properties()))
			return false;
		return true;
	}
	
	
}
