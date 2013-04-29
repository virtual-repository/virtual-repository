package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * Publishes {@link Asset}s through a {@link RepositoryService}, the <em>bound service</em>.
 * <p>
 * A publisher handles assets of a given {@link AssetType}, the <em>bound type</em>, expecting their data under a given API,
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
	 * @param data the data of the asset
	 * 
	 * @Exception if the asset cannot be published
	 */
	void publish(T asset, A data) throws Exception;
}
