package org.fao.virtualrepository;

import static org.fao.virtualrepository.Utils.*;

/**
 * A named property with a typed value and a description.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type of the property value
 */
public class Property<T> {

	private final String name;
	private final String description;
	private final T value;
	
	/**
	 * Creates an instance with a given name and value.
	 * @param name the name
	 * @param value the value
	 */
	public Property(String name, T value) {
		this(name,value,null);
	}
	
	/**
	 * Creates an instance with a given name, value, and description.
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	public Property(String name, T value, String description) {
		
		notNull("property name",name);
		notNull("property value",value);
		
		this.name=name;
		this.description=description;
		this.value=value;
	}
	
	/**
	 * Returns the name of this property.
	 * @return the name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Returns the value of this property.
	 * 
	 * @return the value
	 */
	public T value() {
		return value;
	}
	
	/**
	 * Returns the description of this property.
	 * 
	 * @return the description
	 */
	public String description() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Property<?> other = (Property<?>) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

}
