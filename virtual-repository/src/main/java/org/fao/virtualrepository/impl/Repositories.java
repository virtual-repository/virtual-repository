package org.fao.virtualrepository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repositories {

	public static Logger log = LoggerFactory.getLogger(Repositories.class);
	
	
	private final Map<String,Repository> repositories = new HashMap<String, Repository>();
	
	
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>,Set<Reader>> readers = new HashMap<Class<? extends AssetType>,Set<Reader>>();
	
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AssetType>,Set<Writer>> writers = new HashMap<Class<? extends AssetType>,Set<Writer>>();
	
	public int add(Repository ... repositories) {
		
		int added = 0;
		
		for (Repository repository : repositories)
			if (this.contains(repository.name())) {
				log.warn("cannot load repository {} ({}), as a repository with the same name already exists",repository.name(),repository);
				continue;
			}
			else {
				
				this.repositories.put(repository.name(),repository);
				
				for (Reader reader : repository.readers())
					addReader(reader);
				
				for (Writer writer : repository.writers())
					addWriter(writer);
				
				
				log.info("added repository {} ({})",repository.name(),repository);
				added++;
			}
		
		return added;
	}
	
	//helper
	private void addReader(Reader reader) {
		
		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = reader.type().getClass(); 
		
		if (!readers.containsKey(typeClass)) {
			readers.put(typeClass,new HashSet<Reader>());
		}
		
		readers.get(typeClass).add(reader);
	}
	
	//helper
	private void addWriter(Writer writer) {
		
		@SuppressWarnings("rawtypes")
		Class<? extends AssetType> typeClass = writer.type().getClass(); 
		
		if (!writers.containsKey(typeClass)) {
			writers.put(typeClass,new HashSet<Writer>());
		}
		
		writers.get(typeClass).add(writer);
	}
	
	
	public void load() {
		
		ServiceLoader<Repository> repositories = ServiceLoader.load(Repository.class);
		
		int size = this.repositories.size();
		
		for (Repository repository : repositories)
			add(repository);
		
		log.info("loaded {} repositories",this.repositories.size()-size);
	}
	
	
	
	
	public boolean contains(String name) {
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
	
	
	public Set<Reader> readers(AssetType<?> type) {
		
		return this.readers.containsKey(type.getClass())?
				 this.readers.get(type.getClass()):
				 Collections.<Reader>emptySet();
		
	}
	
	
	public Set<Writer> writers(AssetType<?> type) {
		
		return this.writers.containsKey(type.getClass())?
				 this.writers.get(type.getClass()):
				 Collections.<Writer>emptySet();
		
	}
	
}
