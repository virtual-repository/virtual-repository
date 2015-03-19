package smallgears.virtualrepository;

import static java.util.Arrays.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static smallgears.api.Apikit.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import smallgears.api.group.Group;
import smallgears.virtualrepository.spi.VirtualPlugin;

/**
 * A collection of uniquely named repositories, optionally loaded from the classpath.
 * <p>
 * Repositories can be added, removed, and reloaded at any time. 
 * <p>
 * This class is not thread-safe. If any, synchronisation requirements are for clients to address.
 */
@Slf4j(topic="virtual-repository")
public class Repositories extends Group<Repository,Repositories> {
	
	
	private List<VirtualPlugin> plugins = new ArrayList<>();

	public Repositories(@NonNull Repository ... repositories) {
		this(asList(repositories));
	}
	
	public Repositories(@NonNull Iterable<Repository> repositories) {
		super(Repository::name);
		add(repositories);
	}
	
	
	//add hook
	protected void add(@NonNull Repository repo) {

		if (this.has(repo))
			log.warn("repository {} overwrites {}", repo, this.get(repo.name()));

		try {
				
			repo.proxy().init();
				
		}
		catch(Exception e) {
			log.error("discarding repository "+repo.name()+" as it cannt be initialised (see cause)",e);
			return;
		}
		
		
		validate(repo);
		
		super.add(repo);

		log.info("added repository: {}", repo, repo);

	}
	
	//remove hook
	@Override
	protected Repository remove(String name) {
		

		Repository repo = super.remove(name);
		
		try {
			
		 repo.proxy().shutdown();
			
		}
		catch(Throwable t) {
			log.warn("no clean shutdown for "+name+" (see cause)",t);
		}
		
		return repo;
		
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
		
		log.info(plugins.isEmpty() ? 
				"no plugins found on classpath!":
				"loaded {} repositories out of {} plugin(s)", size()-current,plugins.size());
		
		return this;
	}
	
	
	/**
	 * The repositories that take given types.
	 */
	public Set<Repository> sinks(AssetType... types) {
		
		return elements().stream().filter(Repository::ingests).collect(toSet());			
	}

	/**
	 * The repositories that return given types.
	 */
	public Set<Repository> sources(AssetType... types) {
		
		return elements().stream().filter(Repository::disseminates).collect(toSet());			
	}
	
	/**
	 * Tell all repositories and plugins to shutdown (if they want to know about it).
	 */
	public void shutdown() {
		
		forEach(this::remove);
		
		plugins.stream().forEach(p-> {

				try {
					
					shutdown();
				}
				catch(Throwable t) {
					log.warn("no clean shutdown for plugin "+p.getClass()+" (see cause)",t);
				}
		});			
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private void load(VirtualPlugin plugin) throws Exception {
		
		plugin.init();
		
		Collection<Repository> repos = plugin.repositories();
		
		if (repos==null || repos.isEmpty())
			log.error("plugin {} exports no repositories and will be ignored",plugin.getClass());
		
		else {
			
			add(repos);
			
			plugins.add(plugin);
		}
	}
		
	
	private void validate(Repository repo) throws IllegalArgumentException {
		
		try {
			
			requireNonNull(repo.proxy().browser(),"browser");
			requireNonNull(repo.proxy().readers(),"readers");
			requireNonNull(repo.proxy().writers(),"writers");
			
			if (repo.proxy().readers().isEmpty() && repo.proxy().writers().isEmpty())
				throw new IllegalStateException("service defines no readers or writers");
			
		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid repository "+repo.name()+" (see cause)",e);
		}
	}

}
