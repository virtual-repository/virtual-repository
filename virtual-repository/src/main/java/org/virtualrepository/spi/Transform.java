package org.virtualrepository.spi;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Converts the content of assets from one API (the source) to another (the target).
 * <p>
 * Can be composed with other transforms.
 */
public interface Transform<IN, OUT> {

	/**
	 * Transforms the content of an asset.
	 */
	OUT apply(Asset asset, IN content) throws Exception;
	
	
	/**
	 * The type of assets for which this transform applies.
	 */
	AssetType type();

	/**
	 * The source API.
	 */
	Class<IN> sourceApi();

	/**
	 * The target API.
	 */
	Class<OUT> targetApi();
	
	/**
	 * .Chains this transform onto another.
	 */
	default <S> Transform<S,OUT> after(Transform<S,IN> previous) {
	
		return new Transform<S,OUT>() {
			
			@Override
			public Class<S> sourceApi() {
				return previous.sourceApi();
			}
			
			@Override
			public AssetType type() {
				return previous.type();
			}
			
			@Override
			public Class<OUT> targetApi() {
				return Transform.this.targetApi();
			}
			
			@Override
			public OUT apply(Asset asset, S input) throws Exception {
				return Transform.this.apply(asset,previous.apply(asset,input));
			}
		};
	
	}
	
	/**
	 * Chains a given transformation onto this one.
	 */
	default <S> Transform<IN,S> then(Transform<OUT,S> previous) {
	
		return previous.after(this);
	
	}
}
