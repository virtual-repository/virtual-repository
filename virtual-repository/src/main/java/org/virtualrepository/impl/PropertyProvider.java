package org.virtualrepository.impl;

import api.tabular.Properties;

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
