package smallgears.virtualrepository.spi;

import java.util.Collection;

import smallgears.virtualrepository.Repository;


/**
 * A plugin's entry point.
 * <p>
 * Provides access to the repositories connected by the plugin.
 *
 */
public interface VirtualPlugin extends Lifecycle {

	
	/**
	 * The repositories connected by the plugin.
	 */
	Collection<Repository> repositories();
	
	
	
	/**
	 * Invoked when the proxy or plugin are de-activated.
	 */
	default void shutdown() throws Exception {}
}
