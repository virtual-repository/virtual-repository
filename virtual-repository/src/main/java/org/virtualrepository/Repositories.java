package org.virtualrepository;

import static java.util.stream.Collectors.*;
import static org.virtualrepository.common.Utils.*;
import static smallgears.api.Apikit.*;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.VirtualPlugin;

import smallgears.api.group.Group;

/**
 * A collection of uniquely named repositories, optionally loaded from the classpath.
 * <p>
 * Repositories can be added, removed, and reloaded at any time. 
 * <p>
 * This class is not thread-safe. If any, synchronisation requirements are for clients to address.
 */
@Slf4j(topic="virtual-repository")
public class Repositories extends Group<Repository,Repositories> {

	public Repositories(Repository ... repositories) {
		super(Repository::name);
		add(repositories);
	}
	
	
	//add hook
	protected void add(@NonNull Repository repo) {

		if (this.has(repo))
			log.warn("repository {} overwrites {}", repo, this.get(repo.name()));

		if (repo.proxy() instanceof Lifecycle)
			
			try {
				Lifecycle.class.cast(repo.proxy()).init();
			}
			catch(Exception e) {
				log.error("repository {} cannot be initialised and will be discarded (see cause)",e);
				return;
			}
		
		
		validate(repo);
		
		super.add(repo);

		log.info("added {}", repo, repo);

	}

	/**
	 * Loads repositories from the classpath.
	 */
	public Repositories load() {

		ServiceLoader<VirtualPlugin> loaded = ServiceLoader.load(VirtualPlugin.class);

		List<VirtualPlugin> plugins =  streamof(loaded).collect(toList());
		
		int current = size();
		
		for (VirtualPlugin plugin : plugins)
			
			try {
				
				load(plugin);
				
			}
			catch(Throwable e) {
				log.error("plugin "+plugin.getClass()+" cannot be activated and will be discarded (see cause)",e);
				continue;
			}
		
		log.info("loaded {} repositories out of {} plugin(s)", size()-current,plugins.size());
		
		return this;
	}
	
	
	/**
	 * The repositories that take given types.
	 */
	public Set<Repository> sinks(AssetType... types) {
		
		return elements().stream().filter(Repository::takes).collect(toSet());			
	}

	/**
	 * The repositories that return given types.
	 */
	public Set<Repository> sources(AssetType... types) {
		
		return elements().stream().filter(Repository::returns).collect(toSet());			
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private void load(VirtualPlugin plugin) throws Exception {
		
		if (plugin instanceof Lifecycle)
			Lifecycle.class.cast(plugin).init();
		
		Collection<Repository> services = plugin.services();
		
		if (services==null || services.isEmpty())
			log.error("plugin {} exports no repositories and will be ignored",plugin.getClass());
		
		else
			
			add(services);
	}
		
	
	private void validate(Repository repo) throws IllegalArgumentException {
		
		try {
			
			notNull("browser",repo.proxy().browser());
			notNull("importers",repo.proxy().readers());
			notNull("publishers",repo.proxy().writers());
			
			if (repo.proxy().readers().isEmpty() && repo.proxy().writers().isEmpty())
				throw new IllegalStateException("service defines no importers or publishers");
			
		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid repository service",e);
		}
	}

}
