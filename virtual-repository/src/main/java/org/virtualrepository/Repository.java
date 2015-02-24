package org.virtualrepository;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.VirtualProxy;

import smallgears.api.properties.Properties;

/**
 * Represents a repository with ingestion and dissemination APIs.
 * <p>
 * It's a descriptive wrapper around plugin-provided repository proxies.
 */
@RequiredArgsConstructor
@ToString(of={"name","properties"})
public class Repository {
	
	
	@NonNull @Getter
	private final String name;
	
	@NonNull @Getter
	private final VirtualProxy proxy;
	
	@Getter
	private final Properties properties = Properties.props();
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * All the asset types that can be ingested by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public Set<AssetType> taken(Collection<AssetType> types) {
		
		return filter(proxy.publishers(),types);
	}
	
	/**
	 * All the asset types that can be disseminated by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public Set<AssetType> returned(Collection<AssetType> types) {	
		return filter(proxy.importers(),types);
	}
	
	
	
	////////////////////////////////////////////////////////////////   derived
	
	/**
	 * <code>true</code> if this repository can ingest given asset types.
	 */
	public boolean takes(AssetType ... types) {
		return takes(asList(types));
	}
	
	/**
	 * <code>true</code> if this repository can ingest given asset types.
	 */
	public boolean takes(Collection<AssetType> types) {
		return taken(types).equals(new HashSet<>(types));
	}
	
	
	/**
	 * All the asset types that can be ingested by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public Set<AssetType> taken(AssetType ... types) {
		return taken(asList(types));
	}
	
	 /**
 	 * <code>true</code> if this repository can disseminate given asset types.
 	 */
     public boolean returns(Collection<AssetType> types) {
    	 return returned(types).equals(new HashSet<>(types)); 
     }
     
     /**
  	 * <code>true</code> if this repository can disseminate given asset types.
  	 */
	 public boolean returns(AssetType ... types) {
	 	 return returns(asList(types)); 
	 }
	
	/**
	 * All the asset types that can be disseminated by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public Set<AssetType> returned(AssetType ... types) {
		return returned(asList(types));
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	private Set<AssetType> filter(Collection<? extends Accessor<?>> elements, Collection<AssetType> types) {
		
		return elements.stream()
				        .map(Accessor::type)
				        .filter(t -> types.isEmpty() || types.contains(t))
				        .collect(toSet());
	}
}
