package org.virtualrepository.spi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
	

	/**
	 * Partial implementation.
	 */
	@RequiredArgsConstructor
	static abstract class Abstract<API> implements Accessor<API> {
    

		@NonNull @Getter
		private AssetType type;
		
		@NonNull @Getter
    	private Class<API> api;	
 	
    }
	

}
