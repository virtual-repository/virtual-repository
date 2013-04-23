package org.fao.virtualrepository.spi;

import static org.fao.virtualrepository.Utils.*;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Properties;
import org.fao.virtualrepository.Property;

/**
 * Partial Repository implementation.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class AbstractRepository implements Repository {

	private final QName name;
	private final Properties properties = new Properties();

	/**
	 * Creates an instance with a given name and zero or more properties.
	 * 
	 * @param name the name
	 * @param properties the properties
	 */
	protected AbstractRepository(QName name, Property<?> ... properties) {
		
		notNull(name);
		
		this.name = name;
		this.properties.add(properties);
	}

	@Override
	public QName name() {
		return name;
	}

	@Override
	public Properties properties() {
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
		AbstractRepository other = (AbstractRepository) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	
}
