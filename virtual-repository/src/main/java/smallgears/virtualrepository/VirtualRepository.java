package smallgears.virtualrepository;

import static java.lang.String.*;
import static java.util.Arrays.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import smallgears.api.traits.Streamable;
import smallgears.virtualrepository.common.Constants;
import smallgears.virtualrepository.impl.Extensions;
import smallgears.virtualrepository.impl.Transforms;

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
	 * The transforms available to this repository.
	 */
	Transforms transforms();

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

	
	/////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Discovers the assets of given types in the base repositories.
	 */
	 DiscoverClause discover(Collection<AssetType> types);
	
	 
	 /**
	 * Discovers the assets of given types in the base repositories.
	 */
	 default DiscoverClause discover(AssetType... types) {
		return discover(asList(types));
	 }
	
	 
	 //////////////////////////////////////////////////////////////////////////////////	
		

	/**
	 * Tests if the asset can be retrieved with a given API.
	 * 
	 */
	ContentCheckClause canRetrieve(Asset asset);
		
		
	/**
	 * Retrieves the content of an asset in a given API.
	 * 
	 */
	RetrieveAsClause retrieve(Asset asset);

	
	/**
	 * Retrieves the content of an asset in a given API.
	 * 
	 * @throws IllegalStateException if the input does not identify an asset.
	 */
	default RetrieveAsClause retrieve(String id) {
		
		Asset asset = lookup(id)
				      .orElseThrow(()->new IllegalStateException(format("unknown asset with id %s",id)));
		
		return retrieve(asset);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tests if the asset can be retrieved with a given API.
	 * 
	 */
	ContentCheckClause canPublish(Asset asset);

	
	/**
	 * Publishes an {@link Asset} in the base repository bound to the asset.
	 * 
	 * @throws IllegalArgumentException is the asset is not bound to a base repository
	 * @throws IllegalStateException if the asset cannot be published under the given API
	 * @throw RuntimeException if the asset cannot be published due to a communication error
	 */
	PublishWithClause publish(Asset asset);
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Closes this repository and releases its resources.
	 */
	void shutdown();
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	//  DSL
	
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
		void notifying(DiscoveryObserver observer);
		
		
	}

	interface ContentCheckClause {
		
		/**
		 * The required API for asset content.
		 * 
		 */
		boolean as(Class<?> api);
		
		
	}
	
	interface RetrieveAsClause {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_discovery_timeout}.
		 */
		<A> RetrieveModeClause<A> as(Class<A> api);
		
		
	}
	
	interface RetrieveModeClause<A> {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_retrieval_timeout}.
		 */
		RetrieveModeClause<A> timeout(Duration timeout);
		
		/**
		 * Blocks until the content is retrieved.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be retrieved with the given API
		 * @throw RuntimeException if the content cannot be retrieved due to a communication error.
		 * The underlying cause captures the error, and is a TimeoutException if the error is due
		 * to an expired timeout.
		 */
		A blocking();
		
		/**
		 * Starts retrieving the content asynchronously.
		 * <p>
		 * Any timeout already set is ignored, specify it instead on the returned future.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be retrieved with the given API
		 */
		Future<A> withoutBlocking();
		
		/**
		 * Starts content retrieval asynchronously and notifies an observer of retrieval events.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be retrieved with the given API
		 */
		void notifying(RetrievalObserver<A> observer);
		
	}
	
	
	interface PublishWithClause {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_discovery_timeout}.
		 */
		PublishModeClause with(Object content);
		
		
	}
	
	interface PublishModeClause {
		
		/**
		 * Sets the timeout, overriding {@link Constants#default_publish_timeout}.
		 */
		PublishModeClause timeout(Duration timeout);
		
		/**
		 * Blocks until the content is retrieved.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be published with the given API
		 * @throw RuntimeException if the content cannot be published due to a communication error.
		 * The underlying cause captures the error, and is a TimeoutException if the error is due
		 * to an expired timeout.
		 */
		void blocking();
		
		/**
		 * Starts publishing the content asynchronously.
		 * <p>
		 * Any timeout already set is ignored, specify it instead on the returned future.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be published with the given API
		 */
		Future<?> withoutBlocking();
		
		/**
		 * Starts publishing the content asynchronously and notifies an observer of completion events.
		 * 
		 * @throws IllegalArgumentException is the asset is not bound to a base repository
		 * @throws IllegalStateException if the content cannot be published with the given API
		 */
		void notifying(PublicationObserver observer);
		
	}
	
	/**
	 * Observes discovery processes.
	 */
	public interface DiscoveryObserver {
		
		/**
		 * A new asset has been discovered.
		 */
		default void onNext(Asset event) {};
		
		/**
		 * Discovery is over.
		 */
		default void onCompleted(){};
		
	}
	
	/**
	 * Observes retrieval or publication processes.
	 */
	public interface RetrievalObserver<A> {
		
		/**
		 * Content has been retrieved.
		 */
		default void onSuccess(A event) {};
		
		/**
		 * @throws InterruptedException if the process is interrupted mid-flight 
		 * @throws TimeoutException if timeout expires
		 * @throws Throwable any other show stopper
		 */
		default void onError(Throwable t){};
		
	}
	
	/**
	 * Observes retrieval or publication processes.
	 */
	public interface PublicationObserver {
		
		/**
		 * Content has been published.
		 */
		default void onSuccess() {};
		
		/**
		 * @throws InterruptedException if the process is interrupted mid-flight 
		 * @throws TimeoutException if timeout expires
		 * @throws Throwable any other show stopper
		 */
		default void onError(Throwable t){};
		
	}

}
