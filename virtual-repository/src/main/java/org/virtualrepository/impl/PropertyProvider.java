package org.virtualrepository.impl;

import smallgears.api.properties.Properties;

public interface PropertyProvider {
	
	public static class Simple implements PropertyProvider {
		
		Properties properties = Properties.props();
		
		@Override
		public Properties properties() {
			return properties;
		}
		
	}

	
	Properties properties();
}
