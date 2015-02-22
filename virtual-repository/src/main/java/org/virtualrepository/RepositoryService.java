package org.virtualrepository;

import java.util.Set;

import org.virtualrepository.impl.DefaultRepositoryService;
import org.virtualrepository.spi.ServiceProxy;

import smallgears.api.properties.Properties;

/**
 * Represents a repository with ingestion and dissemination APIs.
 */
public interface RepositoryService {
	
	/**
	 * Creates a repository.
	 */
	static RepositoryService service(String name, ServiceProxy proxy) {
		
		return new DefaultRepositoryService(name,proxy);
	}
	
	
	String name();
	
	Properties properties();
	
	ServiceProxy proxy();
	
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
