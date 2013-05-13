package org.virtualrepository.sdmx;

import static org.virtualrepository.Utils.*;

import java.net.URI;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;

/**
 * Partial implementation of an {@link Asset} available in the SDMX format.
 * 
 * @author Fabio Simeoni
 * 
 */
public class SdmxAsset extends AbstractAsset {


	public static final String agency = "agency";
	public static final String uri = "uri";
	public static final String status = "status";
	
	private final String version;
	private final String remoteId;
	
	private static final String name = "sdmx/generic";
	
	/**
	 * The generic type of {@link SdmxAsset}s.
	 */
	public static final Type<SdmxAsset> type = new AbstractType<SdmxAsset>(name) {};
	
	/**
	 * Creates an instance with a given {@link AssetType}, URN, identifier, version, name, and zero or more properties.
	 * 
	 * @param type the type
	 * @param urn the URN
	 * @param name the identifier
	 * @param name the version
	 * @param name the name
	 * @param properties the properties
	 */
	protected <T extends SdmxAsset> SdmxAsset(Type<T> type,String urn, String id, String version, String name, Property ... properties) {
		
		super(type,urn,name,properties);
		
		notNull("identifier",id);
		this.remoteId=id;
		
		notNull("version",version);
		this.version=version;
	}
	
	/**
	 * Creates an instance with the generic {@link #type}, a given {@link AssetType}, URN, identifier, version, name, and {@link RepositoryService}.
	 * 
	 * @param urn the URN
	 * @param name the identifier
	 * @param name the version
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	protected <T extends SdmxAsset> SdmxAsset(String urn, String id, String version, String name, Property ... properties) {
		
		this(type,urn,id,version,name,properties);
				
	}
	
	/**
	 * Creates an instance with a given {@link AssetType} and {@link RepositoryService}, suitable for asset publication only.
	 * 
	 * 
	 * @param type the type
	 * @param name the name of the asset
	 * @param service the service
	 */
	public <T extends SdmxAsset> SdmxAsset(Type<T> type,String name,RepositoryService service) {
		this(type,"unused","unused","unused",name);
		setService(service);
	}
	
	/**
	 * Returns the identifier of this asset's agency.
	 * 
	 * @return the agency
	 */
	public String agency() {

		return properties().lookup(agency).value(String.class);

	}

	/**
	 * Sets the identifier of this asset's agency.
	 * 
	 * @param name the agency identifier
	 */
	public void setAgency(String id) {

		notNull("agency",id);

		properties().add( new Property(agency,id, "asset's agency"));
	}
	
	/**
	 * Returns the URI of this asset.
	 * 
	 * @return the URI
	 */
	public URI uri() {

		return properties().lookup(uri).value(URI.class);

	}
	
	/**
	 * Sets the URI of this asset.
	 * 
	 * @param u the URI
	 */
	public void setURI(URI u) {

		notNull("uri",u);

		properties().add(new Property(uri,u, "asset's URI"));
	}
	
	/**
	 * Returns the version of this asset.
	 * 
	 * @return the version
	 */
	public String version() {

		return version;

	}
	
	/**
	 * Returns the remote identifier of this asset.
	 * 
	 * @return the identifier
	 */
	public String remoteId() {

		return remoteId;

	}

	
	/**
	 * Returns the status of this asset.
	 * 
	 * @return the status
	 */
	public String status() {

		return properties().lookup(status).value(String.class);

	}
	
	
	/**
	 * Sets the status of this asset.
	 * 
	 * @param s the status
	 */
	public void setStatus(String s) {

		properties().add(new Property(status,s, "asset's status"));

	}

}
