package org.virtualrepository;

import java.util.List;
import java.util.Map;

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
 * 
 * 
 */
public interface VirtualRepository extends Streamable<Asset> {


	/**
	 * The base repositories of this repository.
	 */
	Repositories repositories();

	/**
	 * Discovers the assets of given types in the base repositories.
	 * <p>
	 * Uses a default timeout.
	 * 
	 * @return the number of (newly) discovered assets
	 */
	int discover(AssetType... types);
	
	
	/**
	 * Discovers the assets of given types in a subset of the base repositories.
	 * <p>
	 * Uses a default timeout.
	 * 
	 * @return the number of (newly) discovered assets
	 */
	int discover(Iterable<Repository> services, AssetType... types);
	
	/**
	 * Discovers the assets of given types in the base repositories.
	 * <p>
	 * Failed interactions with given repositories are silently tolerated.
	 * 
	 * @return the number of (newly) discovered assets
	 */
	int discover(long timeout, AssetType... types);
	
	/**
	 * Discovers the assets of given types in the base repositories.
	 * 
	 * @return the number of (newly) discovered assets
	 */
	int discover(long timeout, Iterable<Repository> services, AssetType... types);

	/**
	 * An asset which has been previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 * @throws IllegalStateException if no asset with the given identifier has been previously discovered.
	 */
	Asset lookup(String id);
	
	
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
}
