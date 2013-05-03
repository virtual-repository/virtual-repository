package org.virtualrepository;

import org.virtualrepository.impl.Repositories;

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
	 * Returns the repository services underlying this repository.
	 * 
	 * @return
	 */
	Repositories repositories();

	/**
	 * Discovers all the assets of given types which are available through the repository services.
	 * <p>
	 * Discovery <em>may</em> involve networked interactions with repository services, and typically will. Failures that
	 * occur when interacting with given repository services are silently tolerated. The interactions do <em>not</em>
	 * imply the transfer of asset content, however, only content descriptions.
	 * <p>
	 * This method may be invoked multiple times in the lifetime of this repository, typically to discover new assets that
	 * may have become available through the repository services. In this case, discovering an asset
	 * overwrites any existing description of an asset with the same identifier.
	 * 
	 * @param types the asset types
	 * @return the number of (newly) discovered assets
	 */
	int discover(AssetType<?>... types);

	/**
	 * Returns an asset previously discovered.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 * @param id the asset identifier
	 * @return the asset
	 * 
	 * @throws IllegalStateException if an asset with the given identifier was not ingested in this repository
	 */
	Asset lookup(String id);

	/**
	 * Retrieves the content of a given asset from the repository service bound to the asset, under a given API.
	 * <p>
	 * Retrieval <em>may</em> involve networked interactions with the repository service, and typically will. Failures are
	 * reported as unchecked exceptions.
	 * 
	 * @param asset the asset
	 * @param api the API
	 * @return the content of the asset
	 * 
	 * @throws IllegalStateException if the content of the asset cannot be retrieved with the given API
	 * @throw RuntimeException if the content of the asset cannot be retrieved due to a communication error
	 */
	<A> A retrieve(Asset asset, Class<A> api);

	/**
	 * Publishes a given asset in the repository service bound to the asset.
	 * <p>
	 * Publication <em>may</em> involve networked interactions with the repository service, and typically will. Failures are
	 * reported as unchecked exceptions.
	 * 
	 * @param asset the asset
	 * @param content the content of the asset
	 * 
	 * @throws IllegalStateException if the asset cannot be published under the API of the content provided
	 * @throw RuntimeException if the asset cannot be published due to a communication error
	 */
	void publish(Asset asset, Object content);
}
