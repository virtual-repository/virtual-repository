package org.virtualrepository.spi;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Retrieves the content of assets from their bound repositories.
 * <p>
 * Works specifically with assets of a given type and with content in given API, though it 
 * can be adapted to work with different APIs.
 *
 */
public interface VirtualReader<T extends Asset,A> extends Accessor<A> {

	/**
	 * Retrieves the content a given asset.
	 */
	A retrieve(T asset) throws Exception;
	

	/**
	 * Transforms this reader into another.
	 */
	default <S> VirtualReader<T,S> adaptWith(Transform<T, A,S> transform) {
	
		return ReaderAdapter.adapt(this,transform);
	}
	
	/**
	 * Derives other readers from this reader, based on given transforms.
	 */
	default List<VirtualReader<T,?>> adaptWith(List<Transform<T, A,?>> transforms) {
	
		//cannot use varargs here as @SafeVarargs is not permissable on default methods
		return transforms.stream().map(t->this.adaptWith(t)).collect(toList());
	}
	
	
	
	/**
	 * Partial implementation.
	 */
	static abstract class Abstract<A extends Asset,API> extends Accessor.Abstract<API> implements VirtualReader<A, API> {
    
			public Abstract(AssetType type, Class<API> api) {
				super(type,api);
			}
 	
    }

}
