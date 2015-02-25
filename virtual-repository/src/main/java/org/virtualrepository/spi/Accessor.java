package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * The common interface of {@link VirtualReader}s and {@link VirtualWriter}s.
 * <p>
 * Extensions handle {@link Asset}s of a given {@link AssetType}, the <em>bound type</em>, and expect their content
 * under a given API, the <em>bound API</code>.
 * 
 * @author Fabio Simeoni
 * 
 * @param <A> the assets of the bound type
 * @param <API> the type of the bound API
 */
public interface Accessor<API> extends Comparable<Accessor<?>> {

	/**
	 * Returns the bound type.
	 * 
	 * @return the bound type
	 */
	AssetType type();

	/**
	 * Return the bound API.
	 * 
	 * @return the bound API
	 */
	Class<API> api();
	
	
	@Override
	default int compareTo(Accessor<?> other) {

		int typeorder = type().compareTo(other.type());
		
		return typeorder !=0 ? typeorder : 
							  api()==other.api() ? 0 :
								 api().isAssignableFrom(other.api()) ? 1 : -1;
	}
	

}
