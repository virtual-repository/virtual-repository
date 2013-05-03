package org.fao.virtualrepository.sdmx;

import static org.fao.virtualrepository.Utils.*;

import java.net.URI;

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

	public static final String version = "version";
	public static final String agency = "agency";
	public static final String uri = "uri";
	public static final String status = "status";
	
	/**
	 * Creates an instance with a given type, identifier, name, and repository.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	public SdmxAsset(AssetType<? extends SdmxAsset> type,String id, String name, RepositoryService origin) {
		super(type,id, name, origin,new Property[]{});
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

		return properties().lookup(version,String.class).value();

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
	
	/**
	 * Sets the version of this asset.
	 * 
	 * @param v the version
	 */
	public void setVersion(String v) {

		notNull("version",v);

		properties().add(new Property<String>(version,v, "asset version"));
	}

}
