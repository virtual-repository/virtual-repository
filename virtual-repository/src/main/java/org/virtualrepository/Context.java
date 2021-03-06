package org.virtualrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Threadlocal properties, serve as implicit context for exchanges between clients and plugins. 
 */
public class Context {
	
	private static Logger log = LoggerFactory.getLogger(Context.class);
	
	private static InheritableThreadLocal<Properties> properties = new InheritableThreadLocal<Properties>();
	
	protected Context() {};
	
	public static Properties properties() {
		Properties props = properties.get();
		
		if (props==null) {
			props = new Properties();
			properties.set(props);
		}
		
		return props;
	}
	
	public static void reset() {
		log.debug("resetting contextual properties in thread {}",Thread.currentThread().getId());
		properties.remove();
	}
	
	
	
}
