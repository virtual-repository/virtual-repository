package org.virtualrepository;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

import org.virtualrepository.impl.DefaultVirtualRepository;
import org.virtualrepository.spi.Transform;

@UtilityClass
public class VR {

	/**
	 * A group of repositories.
	 */
	public Repositories repositories(Repository ... repositories) {
		return new Repositories(repositories);
	}
	
	/**
	 * A virtual repository over all the base repositories discovered on the classpath.
	 */
	public VirtualRepository repository() {
		
		return new DefaultVirtualRepository(repositories().load());
	}
	
	/**
	 * A virtual repository over a given set of base repositories.
	 */
	public VirtualRepository repository(Repository ... repositories) {
		
		return new DefaultVirtualRepository(repositories(repositories));
	}
	
	
	/**
	 * A transformation between APIs for the content of given assets.
	 */
	public <A extends Asset> TypeClause<A> transform(Class<A> type) {
		
		return new TypeClause<A>() {
			
			@Override
			public SourceApiClause<A> type(AssetType type) {
				
				return new SourceApiClause<A>() {
			
					@Override
					public <S> TargetApiClause<A, S> from(Class<S> sourceapi) {
						
						return new TargetApiClause<A,S>() {
							
							@Override
							public <T> TransformClause<A, S, T> to(Class<T> targetapi) {
								
								return new TransformClause<A, S, T>() {
									
									@Override
									public Transform<A,S,T> with(BiFunction<A, S, T> transform) {
										return  new Transform<A,S,T>() {
		
											@Override
											public T apply(A asset, S input)throws Exception {
												return transform.apply(asset,input);
											}
											
											@Override
											public AssetType type() {
												return type;
											}
		
											@Override
											public Class<S> sourceApi() {
												return sourceapi;
											}
		
											@Override
											public Class<T> targetApi() {
												return targetapi;
											}
											
											
										};
									}
								};
							}
						};
					}
				};
			}
		};
	}

	public interface TypeClause<A extends Asset> {
		
		/**
		 * The target asset type.
		 */
		SourceApiClause<A> type(AssetType type);

	}
	
	
	public interface SourceApiClause<A extends Asset> {
		
		/**
		 * The API to transform.
		 */
		<S> TargetApiClause<A,S> from(Class<S> sourceapi);

	}
	
	public interface TargetApiClause<A extends Asset,S> {
		
		/**
		 * The transformed API.
		 */
		<T> TransformClause<A,S,T> to(Class<T> targetapi);

	}
	
	public interface TransformClause<A extends Asset,S,T> {
		
		/**
		 * The transformation (asset dependent).
		 */
		Transform<A,S,T> with(BiFunction<A,S,T> transform);
		
		/**
		 * The transformation (asset independent).
		 */
		default Transform<A,S,T> with(Function<S,T> transform) {
			return with((__,stream)->transform.apply(stream));
		}

	}
}
