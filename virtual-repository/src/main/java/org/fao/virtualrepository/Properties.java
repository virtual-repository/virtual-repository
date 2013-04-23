package org.fao.virtualrepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A collection of uniquely named {@link Properties}.
 * 
 * @author Fabio Simeoni
 *
 */
public class Properties implements Iterable<Property<?>> {

	private final Map<String,Property<?>> properties = new HashMap<String, Property<?>>();
	
	@Override
	public Iterator<Property<?>> iterator() {
		return properties.values().iterator();
	}
	
	/**
	 * Adds one or more properties to this collection.
	 * @param properties the properties
	 */
	public synchronized void add(Property<?> ... properties) {
		for (Property<?> property : properties)
			this.properties.put(property.name(),property);
	}
	
	/**
	 * Returns <code>true</code> if this collection contains a given property.
	 * @param name the name of the property
	 * @return <code>true</code> if this collection contains a property with the given name
	 */
	public synchronized boolean contains(String name) {
		return this.properties.containsKey(name);
	}
	
	/**
	 * Returns a propery with a given name and a value of a given type
	 * @param name the name of the property
	 * @param type the type of the value of the property
	 * @return the property
	 * 
	 * @throws IllegalStateException if a property with a given name does not exist in this collection
	 * @throws IllegalArgumentException if a property with the given name exists in this collection but its value does not have the given type
	 */
	public <T> Property<T> lookup(String name, Class<T> type) {
		
		Property<?> property = this.lookup(name);
		
		try {
			type.cast(property.value());
			@SuppressWarnings("unchecked")
			Property<T> typed = (Property<T>) property;
			return typed;
		}
		catch(Exception e) {
			throw new IllegalArgumentException("the value of property "+property.name()+" is not of type "+type);
		}
		
	}
	
	/**
	 * Removes a given property.
	 * @param name the name of the property
	 * 
	 * @throws IllegalStateException if a property with the given name does not exist in this collection
	 */
	public void remove(String name) {
		
		if (this.properties.remove(name)==null)
			throw new IllegalStateException("unknown property "+name);
	}
	
	/**
	 * Returns a given property in this collection.
	 * @param name the name of the property
	 * @return the property
	 * 
	 * @throws IllegalStateException if a property with a given name does not exist in this collection
	 */
	public synchronized Property<?> lookup(String name) {
		
		Property<?> property = this.properties.get(name);
		
		if (property==null)
			throw new IllegalStateException("unknown property "+name);
		
		return property;
		
	}
	
	/**
	 * Returns <code>true</code> if this collection has no properties.
	 * 
	 * @return <code>true</code> if this collection has no properties
	 */
	public synchronized boolean isEmpty() {
		return properties.isEmpty();
	}
	
	/**
	 * Returns the number of properties in this collection
	 * @return the number of properties in this collection
	 */
	public synchronized int size() {
		return properties.size();
	}
	
	
}
