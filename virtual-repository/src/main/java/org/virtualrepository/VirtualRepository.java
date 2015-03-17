package org.virtualrepository;

import static java.util.Arrays.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.virtualrepository.common.Constants;
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
	default DiscoverClause discover(AssetType... types) {
		return discover(asList(types));
	}
	
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
	 * Discovers the assets of given types in the base repositories.
	 */
	 DiscoverClause discover(Collection<AssetType> types);
	
	/**
	 * Retrieves the content of an asset in a given API.
	 * 
	 * @throws IllegalArgumentException is the asset is not bound to a base repository
	 * @throws IllegalStateException if the content cannot be retrieved with the given API
	 * @throw RuntimeException if the content cannot be retrieved due to a communication error
	 */
	RetrieveAsClause retrieve(Asset asset);

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
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_discovery_timeout}.
		 * <p>
		 * The timeout is the max idle time, i.e. longest time in which no new assets are discovered.
		 */
		DiscoverClause timeout(Duration timeout);
		
		/**
		 * Restricts discovery to certain repositories.
		 */
		DiscoverClause over(Repositories repositories);

		/**
		 * Restricts discovery to certain repositories.
		 */
		default DiscoverClause over(Repository ... repositories) {
			
			return over(VR.repositories(repositories));
		}

		/**
		 * Blocks until discovery has completed.
		 * @return the number of new assets discovered.
		 */
		int blocking();
		
		/**
		 * Starts discovering asynchronously.
		 * @return a future of the number of new assets discovered.
		 */
		Future<Integer> withoutBlocking();
		
		/**
		 * Starts discovering asynchronously and notifies an observer of discovery events.
		 */
		void notifying(DiscoveryObserver<Asset> observer);
		
		
	}
	
	
	interface RetrieveAsClause {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_discovery_timeout}.
		 */
		<A> RetrieveModeClause<A> as(Class<A> api);
		
		
	}
	
	interface RetrieveModeClause<A> {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_discovery_timeout}.
		 */
		RetrieveModeClause<A> timeout(Duration timeout);
		
		/**
		 * Blocks until the content is retrieved.
		 */
		A blocking();
		
		/**
		 * Starts retrieving the content asynchronously.
		 */
		Future<A> withoutBlocking();
		
		/**
		 * Starts content retrieval asynchronously and notifies an observer of retrieval events.
		 */
		void notifying(ContentObserver<A> observer);
		
		
	}
	
	
	/**
	 * Observes discovery processes.
	 */
	public interface DiscoveryObserver<A> {
		
		/**
		 * Delivers events.
		 */
		default void onNext(A event) {};
		
		/**
		 * Notifies that no more events will be delivered.
		 */
		default void onCompleted(){};
		
	}
	
	/**
	 * Observes discovery processes.
	 */
	public interface ContentObserver<A> {
		
		/**
		 * Delivers events.
		 */
		default void onSuccess(A event) {};
		
		/**
		 * Notifies that an error has occured, hence no more no more assets will return.
		 */
		default void onError(Throwable t){};
		
	}

}
