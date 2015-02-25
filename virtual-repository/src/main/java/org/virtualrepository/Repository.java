package org.virtualrepository;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.virtualrepository.Types.*;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

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
	public List<AssetType> taken(Collection<AssetType> types) {
		
		return filter(proxy.writers(),types);
	}
	
	/**
	 * All the asset types that can be disseminated by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public List<AssetType> returned(Collection<AssetType> types) {	
	
		return filter(proxy.readers(),types);
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
		return types.stream().allMatch(taken()::contains); 
	}
	
	
	/**
	 * All the asset types that can be ingested by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public List<AssetType> taken(AssetType ... types) {
		return taken(asList(types));
	}
	
	 /**
 	 * <code>true</code> if this repository can disseminate given asset types.
 	 */
     public boolean returns(Collection<AssetType> types) {
    	 
    	 return types.stream().allMatch(returned()::contains); 
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
	public List<AssetType> returned(AssetType ... types) {
		return returned(asList(types));
	}
	

	/**
	 * All the readers for this repositories that can disseminate a given type.
	 * 
	 */
	public List<VirtualReader<?, ?>> readers(@NonNull AssetType type) {

		return accessors( proxy.readers(), type);

	}
	
	
	/**
	 * All the writer for this repositories that can ingest a given type.
	 * 
	 */
	public List<VirtualWriter<?, ?>> writers(@NonNull AssetType type) {

		return accessors(proxy.writers(), type);

	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	private <T extends Accessor<?>> List<T> accessors(Collection<T> elements, @NonNull AssetType type) {
		
		return elements.stream()
				        .filter(r->r.type()==any || r.type().equals(type))
				        .distinct()
				        .collect(toList());
	}

	private List<AssetType> filter(Collection<? extends Accessor<?>> elements, Collection<AssetType> types) {
		
		return elements.stream()
				        .map(Accessor::type)
				        .distinct()
				        .filter(t -> types.isEmpty() || types.contains(t))
				        .collect(toList());
	}
}
