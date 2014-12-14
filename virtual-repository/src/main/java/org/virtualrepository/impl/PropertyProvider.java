package org.virtualrepository.impl;

import org.virtualrepository.Properties;

public interface PropertyProvider {
	
	public static class Simple implements PropertyProvider {
		
		Properties properties = new Properties();
		
		@Override
		public Properties properties() {
			return properties;
		}
		
	}

	
	Properties properties();
}
