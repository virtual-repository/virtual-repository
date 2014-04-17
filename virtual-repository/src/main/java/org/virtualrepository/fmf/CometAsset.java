package org.virtualrepository.fmf;

import org.virtualrepository.Asset;
import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.Type;

/**
 * Partial implementation of an {@link Asset} available in the SDMX format.
 * 
 * @author Fabio Simeoni
 * 
 */
public class CometAsset extends AbstractAsset {

	/**
	 * The generic type of {@link CometAsset}s.
	 */
	public static final Type<CometAsset> type = new CometGenericType();
	
	
	/**
	 * Creates an instance with a given name and properties.
	 * <p>
	 * Inherit as a plugin-facing constructor for asset discovery and retrieval purposes.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	public <T extends CometAsset> CometAsset(String id, String name, Property ... properties) {
		
		super(type,id,name,properties);

	}
	
	/**
	 * Creates an instance with a given name and for a given target service.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication.
	 * 
	 * 
	 * @param name the name
	 * @param service the service
	 */
	public <T extends CometAsset> CometAsset(String name, RepositoryService service, Property ... properties) {
		super(type,name,service,properties);
	}
	
	
	
}
