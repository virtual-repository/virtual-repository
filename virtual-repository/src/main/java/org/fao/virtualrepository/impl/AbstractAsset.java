package org.fao.virtualrepository.impl;

import static org.fao.virtualrepository.Utils.*;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.Properties;
import org.fao.virtualrepository.Property;
import org.fao.virtualrepository.spi.Repository;

/**
 * Partial {@link Asset} implementation.
 * 
 * @author Fabio Simeoni
 *
 * @param <SELF> the type of the concrete implementation
 * @see Asset
 */
public abstract class AbstractAsset<SELF extends AbstractAsset<SELF>> implements Asset {

	private final String id;
	private final String name;
	private final Repository origin;
	private final RepositoryManager manager;
	private final Properties properties = new Properties();
	
	/**
	 * Creates an instance with a given identifier, name, origin and zero or more properties.
	 * @param id the identifier
	 * @param name the name
	 * @param origin the origin
	 * @param properties the properties
	 */
	public AbstractAsset(String id, String name, Repository origin, Property<?> ... properties) {
		
		notNull("asset identifier",id);
		this.id=id;
		
		notNull("asset name",id);
		this.name=name;
		
		notNull("asset repository",id);
		this.origin=origin;
		
		this.manager = new RepositoryManager(origin);
		
		this.properties.add(properties);
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@Override
	public Repository origin() {
		return origin;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public abstract AssetType<SELF> type();
	

	@Override
	public <A> A data(Class<A> api) {		
		
		@SuppressWarnings("unchecked") //we rely on subclasses instantiating SELF parameter correctly
		SELF _this = (SELF) this;
		
		return manager.reader(type(), api).fetch(_this);
	}


	
	@Override
	public Properties properties() {
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAsset<?> other = (AbstractAsset<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	
	
	
	
}
