package org.virtualrepository.spi;

import org.virtualrepository.Asset;

/**
 * Retrieves the content of assets from their bound repositories.
 * <p>
 * Works specifically with assets of a given types, and with content in given API.
 *
 */
public interface VirtualReader<T extends Asset,A> extends Accessor<A> {

	/**
	 * Retrieves the content a given asset.
	 */
	A retrieve(T asset) throws Exception;

}
