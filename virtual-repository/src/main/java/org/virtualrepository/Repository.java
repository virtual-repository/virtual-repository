package org.virtualrepository;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.virtualrepository.common.Utils.*;
import static org.virtualrepository.common.Utils.Comparison.*;

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
 * Describes a repository with ingestion and dissemination APIs.
 * <p>
 * Can access the APIs by proxy.
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
	public List<VirtualReader<?, ?>> readersFor(@NonNull AssetType type) {

		return proxy.readers()
				   .stream()
				   .filter(r->asList(EQUALS,SUBTYPE).contains(compare(type,r.type()))) //supertype check
			       .distinct()
			       .collect(toList());

	}
	
	
	/**
	 * All the readers for this repositories that can disseminate a given type with a given API.
	 * 
	 */
	public <A> List<VirtualReader<Asset, A>> readersFor(@NonNull AssetType type, @NonNull Class<? extends A> api) {

		@SuppressWarnings("all")
		List<VirtualReader<Asset,A>> readers = (List)
				readersFor(type)
				.stream()
				.filter(r->api.isAssignableFrom(r.api()))
				.collect(toList());

		return readers;
	}
	
	
	/**
	 * All the writer for this repositories that can ingest a given type.
	 * 
	 */
	public List<VirtualWriter<?, ?>> writersFor(@NonNull AssetType type) {

		return proxy.writers()
			   .stream()
			   .filter(r->asList(EQUALS,SUPERTYPE).contains(compare(type,r.type()))) //subtype check
		       .distinct()
		       .collect(toList());

	}
	
	/**
	 * All the writer for this repositories that can ingest a given type in a given API.
	 * 
	 */
	public <A> List<VirtualWriter<Asset,A>> writersFor(@NonNull AssetType type, @NonNull Class<? extends A> api) {

		@SuppressWarnings("all")
		List<VirtualWriter<Asset,A>> writers = (List) 
				writersFor(type)
				.stream()
				.filter(r->r.api().isAssignableFrom(api))
				.collect(toList());

		return writers; 
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////


	private List<AssetType> filter(Collection<? extends Accessor<?>> elements, Collection<AssetType> types) {
		
		return elements.stream()
				        .map(Accessor::type)
				        .distinct()
				        .filter(t -> types.isEmpty() || types.contains(t))
				        .collect(toList());
	}
	
}
