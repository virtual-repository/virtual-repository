package org.virtualrepository;

import java.util.Set;

import org.virtualrepository.impl.DefaultRepository;
import org.virtualrepository.spi.VirtualProxy;

import smallgears.api.properties.Properties;

/**
 * Represents a repository with ingestion and dissemination APIs.
 */
public interface Repository {
	
	/**
	 * Creates a repository.
	 */
	static Repository repository(String name, VirtualProxy proxy) {
		
		return new DefaultRepository(name,proxy);
	}
	
	
	String name();
	
	Properties properties();
	
	VirtualProxy proxy();
	
	/**
	 * <code>true</code> if this repository can ingest (at least) one of given asset types.
	 */
	boolean takes(AssetType ... types);
	
	/**
	 * All the asset types that can be ingested by this repository.
	 */
	Set<AssetType> typesTaken();
	
	/**
	 * <code>true</code> if this repository can disseminate (at least) one of given asset types.
	 */
	boolean returns(AssetType ... types);
	
	/**
	 * All the asset types that can be ingested by this repository.
	 */
	Set<AssetType> typesReturned();
	
}
