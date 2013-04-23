package org.fao.virtualrepository.impl;

import static org.fao.virtualrepository.Utils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.spi.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The collection of {@link Repository}s underlying a {@link VirtualRepository}.
 * <p>
 * Repositories can be explicitly added to the collection (cf. {@link #add(Repository...)}, or else discovered on the
 * classpath through the standard {@link ServiceLoader} mechanism (cf. {@link #load()}).
 * <p>
 * Repositories must be uniquely named, and repositories are overwritten by others with the same name which are
 * subsequently added or loaded.
 * <p>
 * This class is thread-safe.
 * 
 * @author Fabio Simeoni
 * @see VirtualRepository
 * @see ServiceLoader
 */
public class Repositories implements Iterable<Repository> {

	public static Logger log = LoggerFactory.getLogger(Repositories.class);

	
	private final Map<QName, Repository> repositories = new HashMap<QName, Repository>();

	/**
	 * Adds one or more {@link Repository}s to this collection, overwriting those that have the same names.
	 * 
	 * @param repositories the repositories
	 * @return the number of repositories effectively added.
	 */
	public synchronized int add(Repository... repositories) {

		notNull("repositories", repositories);

		int added = 0;

		for (Repository repository : repositories) {

			QName name = repository.name();

			if (this.contains(name))
				log.warn("repository {} ({}) overwrites repository with the same name ({})", repository.name(),
						this.lookup(repository.name()));

			this.repositories.put(repository.name(), repository);

			log.info("added repository {} ({})", repository.name(), repository);

			added++;

		}

		return added;
	}

	/**
	 * Adds to this collection all the {@link Repository}s found in the classpath by a {@link ServiceLoader}.
	 */
	public synchronized void load() {

		ServiceLoader<Repository> repositories = ServiceLoader.load(Repository.class);

		int size = this.repositories.size();

		for (Repository repository : repositories)
			add(repository);

		log.info("loaded {} repositories", this.repositories.size() - size);
	}

	/**
	 * Returns <code>true</code> if this collection includes a given {@link Repository}.
	 * 
	 * @param name the name of the repository
	 * @return <code>true</code> if this collection includes the {@link Repository} with the given name
	 */
	public synchronized boolean contains(QName name) {
		
		notNull(name);
		
		return repositories.containsKey(name);
	}

	/**
	 * Returns a {@link Repository} in this collection.
	 * 
	 * @param name the name of the repository
	 * @return the repository with the given name
	 * @throws IllegalStateException if a repository with the given name does not exist
	 */
	public synchronized Repository lookup(QName name) {

		notNull(name);
		
		if (repositories.containsKey(name))
			return repositories.get(name);
		else
			throw new IllegalStateException("source " + name + " is unknown");
	}
	
	
	@Override
	public Iterator<Repository> iterator() {
		return repositories.values().iterator();
	}

}
