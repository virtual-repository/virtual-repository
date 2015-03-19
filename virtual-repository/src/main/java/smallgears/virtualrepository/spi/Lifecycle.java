package smallgears.virtualrepository.spi;

/**
 * Implemented by plugins, proxies, or extensions that require notifications of lifecycle events.
 *
 */
public interface Lifecycle {

	/**
	 * Invoked when the proxy or plugin are first activated.
	 */
	default void init() throws Exception {}
	
	
	/**
	 * Invoked when the proxy or plugin are de-activated.
	 */
	default void shutdown() throws Exception {}
	
}
