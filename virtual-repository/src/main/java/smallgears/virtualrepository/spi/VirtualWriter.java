package smallgears.virtualrepository.spi;

import static java.util.stream.Collectors.*;

import java.util.List;

import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;

/**
 * Publishes the content of assets in their bound repositories.
 * <p>
 * Works specifically with assets of a given type and with content in given API, though it 
 * can be adapted to work with different APIs.
 * 
 * */
public interface VirtualWriter<API> extends Accessor<API> {

	/**
	 * Publishes the content of a given asset.
	 * <p>
	 * The framework ensures the asset has the expected type.
	 */
	void publish(Asset asset, API content) throws Exception;
	
	
	/**
	 * Adapts this writer with a compatible transform.
	 */
	default <S> VirtualWriter<S> adaptWith(Transform<S,API> transform) {
	
		return WriterAdapter.adapt(this,transform);
	}
	
	/**
	 * Adapts this writer with compatible transforms.
	 */
	default List<VirtualWriter<?>> adaptWith(List<Transform<?,API>> transforms) {
	
		//cannot use varargs here as @SafeVarargs is not permissable on default methods
		return transforms.stream().map(t->this.adaptWith(t)).collect(toList());
	}
	
	
	/**
	 * Partial implementation.
	 */
	static abstract class Abstract<API> extends Accessor.Abstract<API> implements VirtualWriter<API> {
    
			public Abstract(AssetType type, Class<API> api) {
				super(type,api);
			}
 	
    }
}
