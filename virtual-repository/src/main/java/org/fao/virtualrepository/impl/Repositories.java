package org.fao.virtualrepository.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.spi.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repositories {

	public static Logger log = LoggerFactory.getLogger(Repositories.class);
	
	
	private final Map<QName,Repository> repositories = new HashMap<QName, Repository>();
	
	public int add(Repository ... repositories) {
		
		int added = 0;
		
		for (Repository repository : repositories)
			
			if (this.contains(repository.name())) {
				
				log.warn("cannot load repository {} ({}), as a repository with the same name already exists",repository.name(),repository);
				continue;
			
			}
			else {
				
				this.repositories.put(repository.name(),repository);				
				log.info("added repository {} ({})",repository.name(),repository);
				added++;
			}
		
		return added;
	}
	
	public void load() {
		
		ServiceLoader<Repository> repositories = ServiceLoader.load(Repository.class);
		
		int size = this.repositories.size();
		
		for (Repository repository : repositories)
			add(repository);
		
		log.info("loaded {} repositories",this.repositories.size()-size);
	}
	
	
	
	
	public boolean contains(QName name) {
		return repositories.containsKey(name);
	}
	
	
	
	public Repository get(String name) {
		
		if (repositories.containsKey(name))
			return repositories.get(name);
		else
			throw new IllegalStateException("source "+name+" is unknown");
	}
	
	
	
	public Collection<Repository> list() {
		return repositories.values(); 
	}
	
	
}
