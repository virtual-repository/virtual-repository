package org.virtualrepository.spi;

import org.virtualrepository.Asset;

/**
 * Retrieves the content assets from their bound repositories, in a given API.
 */
public interface VirtualReader<T extends Asset,A> extends Accessor<A> {

	/**
	 * Retrieves the content a given asset in the bound API.
	 */
	A retrieve(T asset) throws Exception;

}
