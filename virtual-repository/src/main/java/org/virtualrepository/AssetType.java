package org.virtualrepository;



/**
 * A type of {@link Asset}.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the (language) type of assets
 */
public interface AssetType<A extends Asset> {

	/**
	 * Returns the name of this type
	 * @return the name
	 */
	String name();
	
	
}
