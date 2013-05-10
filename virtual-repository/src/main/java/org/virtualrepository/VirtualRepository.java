package org.virtualrepository;

import java.util.Collection;

import org.virtualrepository.impl.Services;

/**
 * A repository virtually comprised of data assets available through a number of underlying <em>repository services</em>
 * .
 * 
 * <p>
 * 
 * Clients may:
 * 
 * <ul>
 * <li> <em>discover</em> all the assets of given types available through the repository services (cf.
 * {@link #discover(AssetType...)}). Asset descriptions can be iterated over or looked up up by identifier (cf.
 * {@link #lookup(String)});
 * 
 * <li> <em>retrieve</em> the content of discovered assets (cf. {@link #retrieve(Asset, Class)});
 * 
 * <li> <em>publish</em> new assets in one of the repository services (cf. {@link #publish(Asset, Object)}).
 * 
 * </ul>
 * 
 * Note that repository services may be remotely accessible, and typically will be. Most of the operations of a virtual
 * repository may then trigger network interactions, as specified in their documentation.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @see Asset
 * @see AssetType
 * @see RepositoryService
 * 
 */
public interface VirtualRepository extends Iterable<Asset> {

	/**
	 * Returns the {@link RepositoryService}s underlying this repository.
	 * 
	 * @return
	 */
	Services services();

	/**
	 * Returns the {@link RepositoryService}s underlying this repository which can publish `Asset`s of at least one of
	 * given {@link AssetType}s.
	 * 
	 * @return the services which can publish `Asset`s of at least one of given {@link AssetType}s
	 */
	Collection<RepositoryService> sinks(AssetType... types);

	/**
	 * Returns the {@link RepositoryService}s underlying this repository which can retrieve `Asset`s of at least one of
	 * given {@link AssetType}s.
	 * 
	 * @return the services which can retrieve `Asset`s of at least one of given {@link AssetType}s
	 */
	Collection<RepositoryService> sources(AssetType... types);

	/**
	 * Discovers all the assets of given {@link AssetType}s which are available through the underlying
	 * {@link RepositoryService}s.
	 * <p>
	 * Discovery <em>may</em> involve networked interactions with repository services, and typically will. Failures that
	 * occur when interacting with given repository services are silently tolerated. The interactions do <em>not</em>
	 * imply the transfer of asset content, however, only content descriptions.
	 * <p>
	 * This method may be invoked multiple times in the lifetime of this repository, typically to discover new assets
	 * that may have become available through the repository services. In this case, discovering an asset overwrites any
	 * existing description of an asset with the same identifier.
	 * 
	 * @param types the asset types
	 * @return the number of (newly) discovered assets
	 */
	int discover(AssetType... types);

	/**
	 * Returns an {@link Asset} previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 * @param name the asset identifier
	 * @return the asset
	 * 
	 * @throws IllegalStateException if an asset with the given identifier was not ingested in this repository
	 */
	Asset lookup(String id);

	/**
	 * Retrieves the content of a given {@link Asset} from the {@link RepositoryService} bound to the asset, under a
	 * given API.
	 * <p>
	 * Retrieval <em>may</em> involve networked interactions with the repository service, and typically will. Failures
	 * are reported as unchecked exceptions.
	 * 
	 * @param asset the asset
	 * @param api the API
	 * @return the content of the asset
	 * 
	 * @throws IllegalArgumentException is the asset has no associated service
	 * @throws IllegalStateException if the content of the asset cannot be retrieved with the given API
	 * @throw RuntimeException if the content of the asset cannot be retrieved due to a communication error
	 */
	<A> A retrieve(Asset asset, Class<A> api);

	/**
	 * Publishes a given {@link Asset} with the {@link RepositoryService} bound to the asset.
	 * <p>
	 * Publication <em>may</em> involve networked interactions with the repository service, and typically will. Failures
	 * are reported as unchecked exceptions.
	 * 
	 * @param asset the asset
	 * @param content the content of the asset
	 * 
	 * @throws IllegalArgumentException is the asset has no associated service
	 * @throws IllegalStateException if the asset cannot be published under the API of the content provided
	 * @throw RuntimeException if the asset cannot be published due to a communication error
	 */
	void publish(Asset asset, Object content);
}
