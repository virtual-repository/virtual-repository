package org.virtualrepository;

import static org.virtualrepository.Utils.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.virtualrepository.impl.PropertyHolder;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.ServiceProxy;

/**
 * A repository service underlying a {@link VirtualRepository}.
 * <p>
 * A repository service has a name, properties, and a {@link ServiceProxy} that can access the service.
 * 
 * @author Fabio Simeoni
 * @see VirtualRepository
 */
public final class RepositoryService extends PropertyHolder {

	private final QName name;
	private final ServiceProxy proxy;
	
	public RepositoryService(QName name, ServiceProxy proxy, Property ... properties) {
		
		this.name=name;
		this.proxy=proxy;
		this.properties().add(properties);
		
		validate();
	}


	/**
	 * Returns the name of this service.
	 * 
	 * @return the name
	 */
	public QName name() {
		return name;
	}
	
	
	/**
	 * Returns the proxy of this service.
	 * @return the proxy
	 */
	public ServiceProxy proxy() {
		return proxy;
	}
	
	/**
	 * Returns <code>true</code> if assets of at least one of given {@link AssetType}s can be published with this service.
	 * @param types the types
	 * @return <code>true</code> if assets of at least one of given {@link AssetType}s can be published with this service
	 */
	public boolean publishes(AssetType ... types) {
		return supports(proxy.publishers(),types);
		
	}
	
	/**
	 * Returns all the {@link AssetType}s that can be published by this service.
	 * @return types the types
	 */
	public Collection<AssetType> publishedTypes() {
		return supported(proxy.publishers());
	}
	
	/**
	 * Returns <code>true</code> if assets of at least one of given {@link AssetType}s can be returned by this service.
	 * @param types the types
	 * @return <code>true</code> if assets of at least one of given {@link AssetType}s can be returned by this service
	 */
	public boolean returns(AssetType ... types) {
		return supports(proxy.importers(),types);
	}
	
	/**
	 * Returns all the {@link AssetType}s that can be returned by this service.
	 * @return types the types
	 */
	public Collection<AssetType> returnedTypes() {
		return supported(proxy.importers());
	}

	//helpers
	
	private void validate() throws IllegalArgumentException {
		
		try {
			
			valid("service name",name);
			
			notNull("service proxy",proxy);
			notNull("browser",proxy.browser());
			notNull("importers",proxy.importers());
			notNull("publishers",proxy.publishers());
			
			if (proxy.importers().isEmpty() && proxy.publishers().isEmpty())
				throw new IllegalStateException("service defines no importers or publishers");
			
		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid repository service",e);
		}
	}
	
	
	private boolean supports(List<? extends Accessor<?,?>> accessors, AssetType... types) {
		
		notNull("asset types",types);
		
		for (AssetType supported : supported(accessors))
			for (AssetType type : types)
				if (supported.equals(type)) 
					return true;

		return false;
	}
	
	private Set<AssetType> supported(List<? extends Accessor<?,?>> accessors) {
		
		Set<AssetType> types = new HashSet<AssetType>();
		for (Accessor<?,?> accessor : accessors)
			types.add(accessor.type());

		return types;
	}
	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((proxy == null) ? 0 : proxy.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryService other = (RepositoryService) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (proxy == null) {
			if (other.proxy != null)
				return false;
		} else if (!proxy.equals(other.proxy))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", proxy=" + proxy + "]";
	}


	
}
