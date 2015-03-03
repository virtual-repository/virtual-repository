package org.virtualrepository.spi;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.virtualrepository.Asset;

/**
 * Publishes the content of assets in their bound repositories.
 * <p>
 * Works specifically with assets of a given type and with content in given API, though it 
 * can be adapted to work with different APIs.
 * 
 * */
public interface VirtualWriter<T extends Asset, A> extends Accessor<A> {

	/**
	 * Publishes the content of a given asset.
	 */
	void publish(T asset, A content) throws Exception;
	
	
	/**
	 * Transforms this writer into another.
	 */
	default <S> VirtualWriter<T,S> adaptWith(Transform<T,S,A> transform) {
	
		return WriterAdapter.adapt(this,transform);
	}
	
	/**
	 * Derives other writers from this writer, based on given transforms.
	 */
	default List<VirtualWriter<T,?>> adaptWith(List<Transform<T,?,A>> transforms) {
	
		//cannot use varargs here as @SafeVarargs is not permissable on default methods
		return transforms.stream().map(t->this.adaptWith(t)).collect(toList());
	}
}
