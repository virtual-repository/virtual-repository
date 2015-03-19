package smallgears.virtualrepository;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import smallgears.api.properties.Properties;
import smallgears.virtualrepository.spi.Accessor;
import smallgears.virtualrepository.spi.VirtualProxy;

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
	
	////////////////////////////////////////////////////////////////////////////////////////


	private List<AssetType> filter(Collection<? extends Accessor<?>> elements, Collection<AssetType> types) {
		
		return elements.stream()
				        .map(Accessor::type)
				        .distinct()
				        .filter(t -> types.isEmpty() || types.contains(t))
				        .collect(toList());
	}
	
}
