package org.virtualrepository.sdmx;

import static org.virtualrepository.Utils.*;

import java.net.URI;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.spi.RepositoryService;

/**
 * Partial implementation of an {@link Asset} available in the SDMX format.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class SdmxAsset extends AbstractAsset {


	public static final String agency = "agency";
	public static final String uri = "uri";
	public static final String status = "status";
	
	private final String version;
	private final String remoteId;
	
	/**
	 * Creates an instance with a given {@link AssetType}, URN, identifier, version, name, and {@link RepositoryService}.
	 * 
	 * @param type the type
	 * @param urn the URN
	 * @param id the identifier
	 * @param id the version
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	public SdmxAsset(AssetType<? extends SdmxAsset> type,URI urn, String id, String version, String name, RepositoryService service, Property<?> ... properties) {
		
		super(type,urn.toString(), name,service,properties);
		
		notNull("identifier",id);
		this.remoteId=id;
		
		notNull("version",version);
		this.version=version;
	}
	
	/**
	 * Creates an instance with a given {@link AssetType} and {@link RepositoryService}, suitable for asset publication only.
	 * 
	 * @param type the type
	 * @param service the service
	 */
	public SdmxAsset(AssetType<? extends SdmxAsset> type, RepositoryService service) {
		this(type,URI.create("http://unused.org"),"unused","unused", "unused", service);
	}
	
	
	/**
	 * Returns the identifier of this asset's agency.
	 * 
	 * @return the agency
	 */
	public String agency() {

		return properties().lookup(agency,String.class).value();

	}

	/**
	 * Sets the identifier of this asset's agency.
	 * 
	 * @param id the agency identifier
	 */
	public void setAgency(String id) {

		notNull("agency",id);

		properties().add( new Property<String>(agency,id, "asset's agency"));
	}
	
	/**
	 * Returns the URI of this asset.
	 * 
	 * @return the URI
	 */
	public URI uri() {

		return properties().lookup(uri,URI.class).value();

	}
	
	/**
	 * Sets the URI of this asset.
	 * 
	 * @param u the URI
	 */
	public void setURI(URI u) {

		notNull("uri",u);

		properties().add(new Property<URI>(uri,u, "asset's URI"));
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

		return properties().lookup(status,String.class).value();

	}
	
	
	/**
	 * Sets the status of this asset.
	 * 
	 * @param s the status
	 */
	public void setStatus(String s) {

		properties().add(new Property<String>(status,s, "asset's status"));

	}

}
