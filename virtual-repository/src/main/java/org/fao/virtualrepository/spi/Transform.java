package org.fao.virtualrepository.spi;

/**
 * A transformation between two APIs.
 * 
 * @author Fabio Simeoni
 *
 * @param <I> the input API
 * @param <O> the output API
 */
public interface Transform<I,O> {

	/**
	 * Transforms an instance of the input API into an instance of the output API
	 * @param input the instance of the input API
	 * @return the instance of the output API
	 */
	O apply(I input);
	
	/**
	 * Returns the input API.
	 * @return the input API
	 */
	Class<I> inputAPI();
	
	/**
	 * Returns the output type.
	 * @return the output type
	 */
	Class<O> outputAPI();
}
