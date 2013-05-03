package org.fao.virtualrepository.sdmx;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.Property;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.RepositoryService;

/**
 * Partial implementation of an {@link Asset} available in the SDMX format.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class SdmxAsset extends AbstractAsset {

	/**
	 * Creates an instance with a given type, identifier, name, and repository.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	public SdmxAsset(AssetType<? extends SdmxAsset> type,String id, String name, RepositoryService origin, Property<?> ... properties) {
		super(type,id, name, origin, properties);
	}
	
	//TODO add descriptive properties of all sdmx assets

}
