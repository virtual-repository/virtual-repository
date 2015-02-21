package org.virtualrepository;

import static api.tabular.Properties.*;
import static org.virtualrepository.Utils.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.ServiceProxy;

import api.tabular.Properties;

/**
 * A repository with ingestion and dissemination APIs.
 * <p>
 * Wraps plugin-specific {@link ServiceProxy}s.
 */
@RequiredArgsConstructor
@Data
public final class RepositoryService {

	@NonNull
	private final String name;
	
	@NonNull
	private final ServiceProxy proxy;
	
	private final Properties properties = props();
	
	/**
	 * Returns <code>true</code> if this repository can ingest (at least) one of given asset types.
	 */
	public boolean publishes(AssetType ... types) {
		return supports(proxy.publishers(),types);
		
	}
	
	/**
	 * Returns all the asset types that can be ingested by this service
	 */
	public Collection<AssetType> publishedTypes() {
		return supported(proxy.publishers());
	}
	
	/**
	 * Returns <code>true</code> if this repository can disseminate (at least) one of given asset types.
	 */
	public boolean returns(AssetType ... types) {
		return supports(proxy.importers(),types);
	}
	
	/**
	 * Returns all the asset types that can be ingested by this service
	 */
	public Collection<AssetType> returnedTypes() {
		return supported(proxy.importers());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
