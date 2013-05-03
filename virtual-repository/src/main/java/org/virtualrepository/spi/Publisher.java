package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Publishes {@link Asset}s through a {@link RepositoryService}, the <em>bound service</em>.
 * <p>
 * A publisher handles assets of a given {@link AssetType}, the <em>bound type</em>, expecting their content under a given API,
 * the <em>bound API</code>.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the bund type
 * @param <A> the bound API
 */
public interface Publisher<T extends Asset, A> extends Accessor<T, A> {

	/**
	 * Publishes an asset through the bound service.
	 * @param asset the asset
	 * @param content the content of the asset
	 * 
	 * @Exception if the asset cannot be published
	 */
	void publish(T asset, A content) throws Exception;
}
