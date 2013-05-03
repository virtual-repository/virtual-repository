package org.virtualrepository.impl;

import static org.virtualrepository.Utils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.Plugin;
import org.virtualrepository.spi.RepositoryService;

/**
 * The collection of {@link RepositoryService}s underlying a {@link VirtualRepository}.
 * <p>
 * Services can be explicitly added to the collection (cf. {@link #add(RepositoryService...)}, or else discovered on the
 * classpath through the standard {@link ServiceLoader} mechanism (cf. {@link #load()}).
 * <p>
 * Services must be uniquely named and are overwritten by others with the same name which are
 * subsequently added or loaded.
 * <p>
 * This class is thread-safe.
 * 
 * @author Fabio Simeoni
 * @see VirtualRepository
 * 
 * @see RepositoryService
 * @see ServiceLoader
 */
public class Repositories implements Iterable<RepositoryService> {

	public static Logger log = LoggerFactory.getLogger(Repositories.class);

	
	private final Map<QName, RepositoryService> services = new HashMap<QName, RepositoryService>();

	/**
	 * Adds one or more {@link RepositoryService}s to this collection, overwriting those that have the same names.
	 * 
	 * @param services the services
	 * @return the number of services effectively added.
	 */
	public synchronized int add(RepositoryService... services) {

		notNull("repository services", services);

		int added = 0;

		for (RepositoryService service : services) {
			
			valid(service);

			QName name = service.name();

			if (this.contains(name))
				log.warn("repository service {} ({}) overwrites service with the same name ({})", service.name(),
						this.lookup(service.name()));

			if (service instanceof Lifecycle)
				try {
					Lifecycle.class.cast(service).init();
				}
				catch(Exception e) {
					log.error("service {} cannot be activated and will be discarded",e);
					continue;
				}
			
			this.services.put(service.name(), service);

			log.info("added repository service {} ({})", service.name(), service);

			added++;

		}

		return added;
	}

	/**
	 * Adds to this collection all the {@link RepositoryService}s found in the classpath by a {@link ServiceLoader}.
	 */
	public synchronized void load() {

		ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class);

		int pluginCount=0;
		int serviceCount=0;
		for (Plugin plugin : plugins) {
			try {

				//initialise plugin
				if (plugin instanceof Lifecycle)
						Lifecycle.class.cast(plugin).init();
				
				List<? extends RepositoryService> services = plugin.services();
	
				if (services==null || services.isEmpty())
					log.error("plugin {} exports no services and will be ignored",plugin.getClass());
				else {
					pluginCount++;
					for (RepositoryService repository : services){ 
						add(repository);
						serviceCount++;
					}
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
	public synchronized boolean contains(QName name) {
		
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
	public synchronized RepositoryService lookup(QName name) {

		notNull(name);
		
		if (services.containsKey(name))
			return services.get(name);
		else
			throw new IllegalStateException("source " + name + " is unknown");
	}
	
	/**
	 * Returns the number of available services.
	 * @return the number of available services
	 */
	public int size() {
		return services.size();
	}
	
	
	@Override
	public Iterator<RepositoryService> iterator() {
		return services.values().iterator();
	}

}
