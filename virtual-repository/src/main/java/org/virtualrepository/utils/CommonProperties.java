package org.virtualrepository.utils;

import org.virtualrepository.Property;

/**
 * An enumeration of commonly used properties.
 *
 */
public enum CommonProperties {

	DESCRIPTION,
	USERNAME;
	
	
	public Property property(String value) {
		return new Property(this.name(), value);
	}
	
	public Property property(String value,String description) {
		return new Property(this.name(), value, description);
	}
}
