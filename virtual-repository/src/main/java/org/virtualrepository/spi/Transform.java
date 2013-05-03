package org.virtualrepository.spi;

import org.virtualrepository.Asset;

/**
 * A transformation between two APIs for a given type of {@link Asset}s.
 * 
 * @author Fabio Simeoni
 * 
 * @param <T> the type of assets
 * @param <I> the input API
 * @param <O> the output API
 */
public interface Transform<T extends Asset, I, O> {

	/**
	 * Transforms an instance of the input API into an instance of the output API for a given asset.
	 * 
	 * @param asset the asset
	 * @param input the instance of the input API
	 * @return the instance of the output API
	 * 
	 * @throws Exception if the transform could not be applied
	 */
	O apply(T asset, I input) throws Exception;

	/**
	 * Returns the input API.
	 * 
	 * @return the input API
	 */
	Class<I> inputAPI();

	/**
	 * Returns the output type.
	 * 
	 * @return the output type
	 */
	Class<O> outputAPI();
}
