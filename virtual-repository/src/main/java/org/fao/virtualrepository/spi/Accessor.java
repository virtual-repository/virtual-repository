package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * The common interface of {@link Importer}s and {@link Publisher}s.
 * <p>
 * Extensions handle {@link Asset}s of a given {@link AssetType}, the <em>bound type</em>, and expect their data
 * under a given API, the <em>bound API</code>.
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the assets of the bound type
 * @param <A> the type of the bound API
 */
public interface Accessor<T extends Asset, A> {

	/**
	 * Returns the bound type.
	 * 
	 * @return the bound type
	 */
	AssetType<T> type();

	/**
	 * Return the bound API.
	 * 
	 * @return the bound API
	 */
	Class<A> api();
}
