package org.fao.virtualrepository;

import javax.xml.namespace.QName;


/**
 * The type of an {@link Asset}.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the language type of the asset
 */
public interface AssetType<A extends Asset> {

	/**
	 * Returns the name of this type
	 * @return the name
	 */
	QName name();
	
	
}
