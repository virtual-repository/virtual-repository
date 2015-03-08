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
public interface VirtualReader<API> extends Accessor<API> {

	/**
	 * Retrieves the content a given asset.
	 * <p>
	 * The framework ensures the asset has the expected type.
	 */
	API retrieve(Asset asset) throws Exception;
	

	/**
	 * Transforms this reader into another.
	 */
	default <S> VirtualReader<S> adaptWith(Transform<API,S> transform) {
	
		return ReaderAdapter.adapt(this,transform);
	}
	
	/**
	 * Derives other readers from this reader, based on given transforms.
	 */
	default List<VirtualReader<?>> adaptWith(List<Transform<API,?>> transforms) {
	
		//cannot use varargs here as @SafeVarargs is not permissable on default methods
		return transforms.stream().map(t->this.adaptWith(t)).collect(toList());
	}
	
	
	
	/**
	 * Partial implementation.
	 */
	static abstract class Abstract<A extends Asset,API> extends Accessor.Abstract<API> implements VirtualReader<API> {
    
			public Abstract(AssetType type, Class<API> api) {
				super(type,api);
			}
 	
    }

}
