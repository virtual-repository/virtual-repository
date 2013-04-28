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

	private String id;
	private String name;
	private Repository origin;
	private Properties properties = new Properties();
	private DataProvider provider;
	
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
		
		this.properties.add(properties);
		
		this.provider = new RemoteProvider();
					
	}
		
	@Override
	public void setData(final Object data) {
		
		notNull("asset data stream",data);
		
		this.provider = new LocalProvider(data);
		
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
		try {
			return provider.get(api);
		}
		catch(Exception e) {
			throw new IllegalStateException("the data is not available with API " + api,e);
		}
	}


	
	@Override
	public Properties properties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return type().name()+" [id=" + id() + ", name=" + name() + (properties.isEmpty()?"":", "+ properties()) + "]";
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
	
	
	//used internally to abstract over local and remote data streams
	private interface DataProvider {
		
		<A> A get(Class<A> api);
	}
	
	//used internally to fetch remote data streams
	private class RemoteProvider implements DataProvider {
		
		@Override
		public <T> T get(Class<T> api) {
			
			RepositoryManager manager = new RepositoryManager(origin);
			
			//we rely on subclasses instantiating SELF parameter correctly
			@SuppressWarnings("unchecked") 
			SELF _this = (SELF) AbstractAsset.this;
			
			return manager.reader(type(), api).fetch(_this);
			
		}
	}	
	
	//used internally to wrap local data streams
	private class LocalProvider implements DataProvider {
			
			Object data;
			
			public LocalProvider(Object data) {
				this.data=data;
			}
			
			@Override
			public <T> T get(Class<T> api) {
				
				if (!api.isAssignableFrom(data.getClass()))
					throw new IllegalStateException("the data cannot be cast to " + api);
				else
					return api.cast(data);
				
			}
	}	
	
}
