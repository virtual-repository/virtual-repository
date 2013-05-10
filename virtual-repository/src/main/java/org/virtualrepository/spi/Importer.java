package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.RepositoryService;

/**
 * Discovers and retrieves the content of assets available through a {@link RepositoryService}, the <em>bound service</em>.
 * <p>
 * The importer handles assets of a given {@link AssetType}, the <em>bound type</em>, and  
 * retrieves their content under a given API, the <em>bound API</code>.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the bound type
 * @param <A> the bound API
 */
public interface Importer<T extends Asset,A> extends Accessor<T, A> {

	/**
	 * Returns the content of a given asset under the bound API.
	 * @param asset the asset
	 * @return the content of the asset
	 * 
	 * @Exception if the data of the asset cannot be retrieved
	 */
	A retrieve(T asset) throws Exception;

}
