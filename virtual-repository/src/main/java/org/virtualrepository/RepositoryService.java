package org.virtualrepository;

import java.util.Collection;

import org.virtualrepository.impl.DefaultRepositoryService;
import org.virtualrepository.spi.ServiceProxy;

import api.tabular.Properties;

/**
 * Represents a repository with ingestion and dissemination APIs.
 */
public interface RepositoryService {
	
	
	static RepositoryService service(String name, ServiceProxy proxy) {
		
		return new DefaultRepositoryService(name,proxy);
	}
	
	/**
	 * The name of the repository.
	 */
	String name();
	
	Properties properties();
	
	
	ServiceProxy proxy();
	
	/**
	 * Returns <code>true</code> if this repository can ingest (at least) one of given asset types.
	 */
	boolean publishes(AssetType ... types);
	
	/**
	 * Returns all the asset types that can be ingested by this service
	 */
	Collection<AssetType> publishedTypes();
	
	/**
	 * Returns <code>true</code> if this repository can disseminate (at least) one of given asset types.
	 */
	boolean returns(AssetType ... types);
	
	/**
	 * Returns all the asset types that can be ingested by this service
	 */
	public Collection<AssetType> returnedTypes();
	
}
