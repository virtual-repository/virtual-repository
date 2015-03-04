package org.virtualrepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.virtualrepository.impl.Extensions;

import smallgears.api.traits.Streamable;

/**
 * A repository virtually comprised of data assets held in other <em>base</em> repositories.
 * .
 * <p>
 * 
 * Clients may:
 * 
 * <ul>
 * <li> <em>discover</em> assets of given types available in base repositories. 
 * <li> <em>retrieve</em> the content of discovered assets;
 * <li> <em>publish</em> new assets in a base repository.
 * 
 * </ul>
 * 
 * As base repositories are typically remote, the operations above trigger network interactions.
 * <p>
 * Underlying this repository, there are <em>type extensions</em> that add support for given asset types.
 * 
 * 
 */
public interface VirtualRepository extends Streamable<Asset> {


	/**
	 * The base repositories of this repository.
	 */
	Repositories repositories();
	
	/**
	 * The type extensions underlying this repository.
	 */
	Extensions extensions();

	/**
	 * Discovers the assets of given types in the base repositories.
	 */
	DiscoverClause discover(AssetType... types);
	
	
	/**
	 * The number of assets discovered so far in this repository.
	 */
	int size();
	
	/**
	 * Returns an asset which has been previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 */
	Optional<Asset> lookup(String id);
	
	
	/**
	 * Returns all the assets of a given types which have been previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 */
	List<Asset> lookup(AssetType type);
	
	
	/**
	 * Returns all the assets of given types which have been previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 */
	Map<AssetType,List<Asset>> lookup(AssetType ... type);

	/**
	 * <code>true</code> if a given asset can be retrieved in a given API.
	 * 
	 */
	boolean canRetrieve(Asset asset, Class<?> api);
	
	
	/**
	 * <code>true</code> if a given asset can be published in a given API.
	 * 
	 */
	boolean canPublish(Asset asset, Class<?> api);
	
	/**
	 * Retrieves the content of an asset in a given API.
	 * 
	 * @throws IllegalArgumentException is the asset is not bound to a base repository
	 * @throws IllegalStateException if the content cannot be retrieved with the given API
	 * @throw RuntimeException if the content cannot be retrieved due to a communication error
	 */
	<A> A retrieve(Asset asset, Class<A> api);

	/**
	 * Publishes an {@link Asset} in the base repository bound to the asset.
	 * 
	 * @throws IllegalArgumentException is the asset is not bound to a base repository
	 * @throws IllegalStateException if the asset cannot be published under the given API
	 * @throw RuntimeException if the asset cannot be published due to a communication error
	 */
	void publish(Asset asset, Object content);
	
	
	/**
	 * Closes this repository and releases its resources.
	 */
	void shutdown();
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	interface DiscoverClause {
		
		DiscoverClause timeout(long timeout);
		
		DiscoverClause over(Repositories repositories);
		
		DiscoverClause over(Repository ... repositories);
		
		int now();
	}
}
