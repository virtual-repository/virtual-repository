package org.virtualrepository.spi;

import org.virtualrepository.Asset;

/**
 * Publishes the content of assets in their bound repositories.
 * <p>
 * <p>
 * Works specifically with assets of a given types, and with content in given API.
 * 
 * */
public interface VirtualWriter<T extends Asset, A> extends Accessor<A> {

	/**
	 * Publishes the content of a given asset.
	 */
	void publish(T asset, A content) throws Exception;
}
