package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * Discovers and retrives data {@link Asset}s from an associated {@link Repository}.
 * <p>
 * The reader discovers assets of a given {@link AssetType}, the <em>bound type</em> (cf. {@link #find()}). 
 * It retrieves their data stream on demand under a given API, the <em>bound API</code> (cf {@link #fetch(Asset)}).
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the type the assets discovered and retrieved by the reader
 * @param <A> the bound API of the the reader
 */
public interface Reader<T extends Asset,A> extends Accessor<T, A> {

	/**
	 * Returns all the {@link Asset}s of the bound type in {@link Repository} associated with this reader.
	 * 
	 * @return the assets
	 */
	Iterable<? extends T> find();

	/**
	 * Returns the data of a given asset under the bound API of this reader.
	 * @param asset
	 * @return
	 */
	A fetch(T asset);

}
