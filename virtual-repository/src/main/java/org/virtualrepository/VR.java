package org.virtualrepository;

import java.util.function.BiFunction;

import lombok.experimental.UtilityClass;

import org.virtualrepository.impl.DefaultVirtualRepository;
import org.virtualrepository.spi.Transform;

@UtilityClass
public class VR {

	/**
	 * A group of repositories.
	 */
	public static Repositories repositories(Repository ... repositories) {
		return new Repositories(repositories);
	}
	
	/**
	 * A virtual repository over all the base repositories discovered on the classpath.
	 */
	public static VirtualRepository repository() {
		
		return new DefaultVirtualRepository(repositories().load());
	}
	
	/**
	 * A virtual repository over a given set of base repositories.
	 */
	public static VirtualRepository repository(Repository ... repositories) {
		
		return new DefaultVirtualRepository(repositories(repositories));
	}
	
	
	/**
	 * A transformation between APIs for the content of given assets.
	 */
	public static <A extends Asset> SourceApiClause<A> transform(Class<A> type) {
		
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
									public Class<S> sourceAPI() {
										return sourceapi;
									}

									@Override
									public Class<T> targetAPI() {
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
	
	
	public static interface SourceApiClause<A extends Asset> {
		
		/**
		 * The API to transform.
		 */
		<S> TargetApiClause<A,S> from(Class<S> sourceapi);

	}
	
	public static interface TargetApiClause<A extends Asset,S> {
		
		/**
		 * The transformed API.
		 */
		<T> TransformClause<A,S,T> to(Class<T> targetapi);

	}
	
	public static interface TransformClause<A extends Asset,S,T> {
		
		/**
		 * The transformation.
		 */
		Transform<A,S,T> with(BiFunction<A,S,T> transform);

	}
}
