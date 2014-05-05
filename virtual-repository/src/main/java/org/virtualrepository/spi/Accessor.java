package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.impl.Type;

/**
 * The common interface of {@link Importer}s and {@link Publisher}s.
 * <p>
 * Extensions handle {@link Asset}s of a given {@link AssetType}, the <em>bound type</em>, and expect their content
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
	Type<? extends T> type();

	/**
	 * Return the bound API.
	 * 
	 * @return the bound API
	 */
	Class<A> api();
}
