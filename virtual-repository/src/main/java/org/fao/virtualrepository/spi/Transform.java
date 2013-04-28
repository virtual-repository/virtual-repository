package org.fao.virtualrepository.spi;

/**
 * A transformation between two types.
 * 
 * @author Fabio Simeoni
 *
 * @param <I> the input type
 * @param <O> the output type
 */
public interface Transform<I,O> {

	/**
	 * Transforms an instance of the input type into an instance of the output type
	 * @param input the instance of the input type
	 * @return the instance of the output type
	 */
	O transform(I input);
	
	/**
	 * Returns the output type.
	 * @return the output type
	 */
	Class<O> api();
}
