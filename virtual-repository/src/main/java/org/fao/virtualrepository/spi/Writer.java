package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;


/**
 * Publishes data {@link Asset}s into an associated {@link Repository}.
 * <p>
 * The writer publishes assets of a given {@link AssetType}, the <em>bound type</em>. 
 * It does so on demand, expecting the data streams of assets to expose a given API, the <em>bound API</code> (cf {@link #fetch(Asset)}).
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the type of the assets bound to this writer
 * @param <A> the bound API of the the writer
 */
public interface Writer<T extends Asset,A> extends Accessor<T, A> {

	//@TODO
}
