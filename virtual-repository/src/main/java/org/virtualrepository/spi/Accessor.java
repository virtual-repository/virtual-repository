package org.virtualrepository.spi;

import org.virtualrepository.AssetType;

/**
 * The common interface of readers and writers.
 * <p>
 * Accessors handle content of given asset types under give APIs.
 */
public interface Accessor<API> {

	/**
	 * The bound type.
	 */
	AssetType type();

	/**
	 * The bound API.
	 */
	Class<API> api();
	

}
