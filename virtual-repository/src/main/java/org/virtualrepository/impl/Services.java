package org.virtualrepository.impl;

import static org.virtualrepository.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.VirtualPlugin;

/**
 * A collection of uniquely named repositories.
 * <p>
 * Repositories can be explicitly added or else discovered by a {@link ServiceLoader}.
 * <p>
 * This class is not thread-safe. If any, synchronisation requirements fall on clients.
 */
@Slf4j
@NoArgsConstructor(staticName="services")
public class Services implements Iterable<RepositoryService> {

	private Map<String,RepositoryService> services = new HashMap<String,RepositoryService>();
	
	public static Services services(RepositoryService ... services) {
		
		Services ss = services();
		
		ss.add(services);
		
		return ss;
		
	}
	/**
	 * Adds one or more {@link RepositoryService}s to this collection, overwriting those that have the same names.
	 * 
	 * @param services the services
	 * @return the number of services effectively added
	 */
	public int add(@NonNull RepositoryService ... services) {

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

		ServiceLoader<VirtualPlugin> plugins = ServiceLoader.load(VirtualPlugin.class);

		int pluginCount=0;
		int serviceCount=0;
		for (VirtualPlugin plugin : plugins) {
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
