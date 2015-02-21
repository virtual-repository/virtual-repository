package org.virtualrepository.impl;

import static org.virtualrepository.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.Plugin;

/**
 * A collection of {@link RepositoryService}s.
 * <p>
 * Services can be explicitly added to the collection (cf. {@link #add(RepositoryService...)}, or else discovered on the
 * classpath through the standard {@link ServiceLoader} mechanism (cf. {@link #load()}).
 * <p>
 * Services must be uniquely named and are overwritten by others with the same name which are
 * subsequently added or loaded.
 * <p>
 * This class is not thread-safe. If any, synchronisation requirements fall on clients.
 * 
 * @author Fabio Simeoni
 * 
 * @see RepositoryService
 * @see ServiceLoader
 */
public class Services implements Iterable<RepositoryService> {

	public static Logger log = LoggerFactory.getLogger(Services.class);

	private final Map<String,RepositoryService> services = new HashMap<String,RepositoryService>();

	/**
	 * Creates an instance with no {@link RepositoryService}s.
	 */
	public Services() {};
	
	/**
	 * Creates an instance with given {@link RepositoryService}s.
	 * @param services the services
	 */
	public Services(RepositoryService ... services) {
		add(services);
	}
	/**
	 * Adds one or more {@link RepositoryService}s to this collection, overwriting those that have the same names.
	 * 
	 * @param services the services
	 * @return the number of services effectively added
	 */
	public int add(RepositoryService ... services) {

		notNull("repository services", services);

		int added = 0;

		for (RepositoryService service : services) {
			
			notNull("repository service", service);

			String name = service.name();

			if (this.contains(name))
				log.warn("repository service {} ({}) overwrites service with the same name ({})", service.name(), service,
						this.lookup(service.name()));

			if (service.proxy() instanceof Lifecycle)
				try {
					Lifecycle.class.cast(service.proxy()).init();
				}
				catch(Exception e) {
					log.error("repository service {} cannot be activated and will be discarded",e);
					continue;
				}
			
			validate(service);
			
			this.services.put(service.name(),service);

			log.info("added repository service {} ({})", service.name(), service);

			added++;

		}

		return added;
	}

	/**
	 * Adds to this collection all the {@link RepositoryService}s found in the classpath by a {@link ServiceLoader}.
	 */
	public void load() {

		ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class);

		int pluginCount=0;
		int serviceCount=0;
		for (Plugin plugin : plugins) {
			try {

				//initialise plugin
				if (plugin instanceof Lifecycle)
						Lifecycle.class.cast(plugin).init();
				
				Collection<? extends RepositoryService> services = plugin.services();
	
				if (services==null || services.isEmpty())
					log.error("plugin {} exports no repository services and will be ignored",plugin.getClass());
				else {
					pluginCount++;
					for (RepositoryService service : services)
						serviceCount=serviceCount+add(service);
					
				}
			}
			catch(Exception e) {
				log.error("plugin "+plugin.getClass()+" cannot be activated and will be discarded",e);
				continue;
			}

		}
		log.info("loaded {} repository service(s) from {} plugin(s)", serviceCount,pluginCount);
	}

	/**
	 * Returns <code>true</code> if this collection includes a given {@link RepositoryService}.
	 * 
	 * @param name the name of the service
	 * @return <code>true</code> if this collection includes the {@link RepositoryService} with the given name
	 */
	public boolean contains(String name) {
		
		notNull(name);
		
		return services.containsKey(name);
	}

	/**
	 * Returns a {@link RepositoryService} in this collection.
	 * 
	 * @param name the name of the service
	 * @return the service with the given name
	 * @throws IllegalStateException if a service with the given name does not exist
	 */
	public RepositoryService lookup(String name) {

		notNull(name);
		
		if (services.containsKey(name))
			return services.get(name);
		else
			throw new IllegalStateException("source " + name + " is unknown");
	}
	
	/**
	 * Returns the number of {@link RepositoryService}s in this collection.
	 * @return the number of available services
	 */
	public int size() {
		return services.size();
	}
	
		
	/**
	 * Returns the {@link RepositoryService}s in this collection.
	 * @return the services
	 */
	public Iterator<RepositoryService> iterator() {
		return new ArrayList<RepositoryService>(services.values()).iterator();
	}
	
	
	//helpers
	
	private void validate(RepositoryService service) throws IllegalArgumentException {
		
		try {
			
			notNull("browser",service.proxy().browser());
			notNull("importers",service.proxy().importers());
			notNull("publishers",service.proxy().publishers());
			
			if (service.proxy().importers().isEmpty() && service.proxy().publishers().isEmpty())
				throw new IllegalStateException("service defines no importers or publishers");
			
		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid repository service",e);
		}
	}

}
