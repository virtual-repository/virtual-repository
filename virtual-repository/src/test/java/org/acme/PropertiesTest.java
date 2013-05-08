package org.acme;

import static org.junit.Assert.*;

import org.junit.Test;
import org.virtualrepository.Properties;
import org.virtualrepository.Property;

public class PropertiesTest {

	@Test
	public void propertiesAreValidCollections() {
		
		Properties properties = new Properties();
		
		assertTrue(properties.isEmpty());
		assertEquals(0,properties.size());
		
		
		for (@SuppressWarnings("unused") Property prop : properties);
		
		Property prop = new Property("test-prop", "tes-value", "a test propery");
		
		assertFalse(properties.contains(prop.name()));
		
		properties.add(prop);
		
		assertTrue(properties.contains(prop.name()));
		assertFalse(properties.isEmpty());
		assertEquals(1,properties.size());
		
		Property retrieved = properties.lookup(prop.name());
		
		assertEquals(prop,retrieved);
		
		try {
			retrieved.value(Integer.class);
			fail();
		}
		catch(IllegalStateException e) {}
		
		try {
			properties.remove("wrong");
			fail();
		}
		catch(IllegalStateException e) {}
		
		properties.remove(prop.name());
		
		assertFalse(properties.contains(prop.name()));
		assertTrue(properties.isEmpty());
		assertEquals(0,properties.size());
	}
}
