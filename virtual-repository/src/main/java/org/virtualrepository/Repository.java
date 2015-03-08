package org.virtualrepository;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.virtualrepository.common.Utils.*;

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
 * A repository with ingestion and dissemination APIs available by proxy.
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
	public List<AssetType> ingested(Collection<AssetType> types) {
		
		return filter(proxy.writers(),types);
	}
	
	/**
	 * All the asset types that can be disseminated by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public List<AssetType> disseminated(Collection<AssetType> types) {	
	
		return filter(proxy.readers(),types);
	}
	
	
	
	////////////////////////////////////////////////////////////////   derived

	/**
	 * All the asset types that can be ingested by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public List<AssetType> ingested(AssetType ... types) {
		return ingested(asList(types));
	}
	
	/**
	 * <code>true</code> if this repository can ingest given asset types.
	 */
	public boolean ingests(AssetType ... types) {
		return ingests(asList(types));
	}
	
	/**
	 * <code>true</code> if this repository can ingest given asset types.
	 */
	public boolean ingests(Collection<AssetType> types) {
		return types.stream().allMatch(ingested()::contains); 
	}
	

	/**
	 * All the asset types that can be disseminated by this repository.
	 * <p>
	 * Optionally filtered by given types.
	 */
	public List<AssetType> disseminated(AssetType ... types) {
		return disseminated(asList(types));
	}
	
	 /**
 	 * <code>true</code> if this repository can disseminate given asset types.
 	 */
     public boolean disseminates(Collection<AssetType> types) {
    	 
    	 return types.stream().allMatch(disseminated()::contains); 
     }
     
     /**
  	 * <code>true</code> if this repository can disseminate given asset types.
  	 */
	 public boolean disseminates(AssetType ... types) {
	 	 return disseminates(asList(types)); 
	 }
	
	/**
	 * All the readers for this repository that can disseminate a given type.
	 * 
	 */
	@SuppressWarnings("all")
	public List<VirtualReader<?>> readersFor(@NonNull AssetType type) {

		//cast is ok: dont keep the output, dont care if/how it's changed.
		return (List) readersFor(type,Object.class); 

	}
	
	
	/**
	 * All the readers for this repository that can disseminate a given type with a given API.
	 * 
	 */
	@SuppressWarnings("all")
	public <A> List<VirtualReader<A>> readersFor(@NonNull AssetType type, @NonNull Class<? extends A> api) {

		 //cast ok: checked @ runtime
		return   (List)
				 proxy.readers().stream()
				.filter(r->ordered(r.type(), type) && ordered(r.api(),api)) //type-then-api checks
				.collect(toList());
	}

	/**
	 * All the APIs in which this repository can disseminate a given type.
	 * 
	 */
	public List<Class<?>> disseminatedFor(@NonNull AssetType type) {

		return readersFor(type).stream().map(r->r.api()).distinct().collect(toList());

	}

	
	/**
	 * All the writers for this repository that can ingest a given type.
	 * 
	 */
	public List<VirtualWriter<?>> writersFor(@NonNull AssetType type) {

		return proxy.writers()
			   .stream()
			   .filter(r->ordered(type,r.type()))
		       .distinct()
		       .collect(toList());

	}
	
	/**
	 * All the writers for this repository that can ingest a given type in a given API.
	 * 
	 */
	public <A> List<VirtualWriter<A>> writersFor(@NonNull AssetType type, @NonNull Class<? extends A> api) {

		@SuppressWarnings("all")
		List<VirtualWriter<A>> writers = (List) 
				writersFor(type)
				.stream()
				.filter(r->r.api().isAssignableFrom(api))
				.collect(toList());

		return writers; 
	}
	
	
	/**
	 * All the APIs in which this repository can ingest a given type.
	 * 
	 */
	public List<Class<?>> ingestedFor(@NonNull AssetType type) {

		return writersFor(type).stream().map(r->r.api()).distinct().collect(toList());

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
