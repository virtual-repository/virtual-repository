package org.fao.virtualrepository.spi;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * Discovers and retrieves the data assets available through a {@link RepositoryService}, the <em>bound service</em>.
 * <p>
 * The importer handles assets of a given {@link AssetType}, the <em>bound type</em>, and  
 * retrieves their data under a given API, the <em>bound API</code>.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the bound type
 * @param <A> the bound API
 */
public interface Importer<T extends Asset,A> extends Accessor<T, A> {

	/**
	 * Returns the data of a given asset under the bound API.
	 * @param asset
	 * @return the data
	 * 
	 * @Exception if the data of the asset cannot be retrieved
	 */
	A retrieve(T asset) throws Exception;

}
