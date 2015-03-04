package org.virtualrepository;

import static java.lang.String.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.virtualrepository.impl.DefaultVirtualRepository;
import org.virtualrepository.impl.Extensions;
import org.virtualrepository.impl.Transforms;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualExtension;

@UtilityClass
public class VR {

	/**
	 * A group of repositories.
	 */
	public Repositories repositories(Repository ... repositories) {
		return new Repositories(repositories);
	}
	
	/**
	 * A pool of transforms.
	 */
	public Transforms transforms(Transform<?,?,?> ... transforms) {
		return new Transforms(transforms);
	}
	
	/**
	 * A group of extensions.
	 */
	public Extensions extensions(VirtualExtension ... extensions) {
		return new Extensions(extensions);
	}
	
	/**
	 * A virtual repository over all the base repositories discovered on the classpath.
	 */
	public VirtualRepository repository() {
		
		return new DefaultVirtualRepository(repositories().load(), extensions().load());
	}
	
	/**
	 * A virtual repository over a given set of base repositories.
	 */
	public VirtualRepository repository(Repository ... repositories) {
		
		return new DefaultVirtualRepository(repositories(repositories),extensions());
	}
	
	/**
	 * A virtual repository over given sets of base repositories and extensions.
	 */
	public VirtualRepository repository(@NonNull Repositories repositories, @NonNull Extensions extensions) {
		
		return new DefaultVirtualRepository(repositories,extensions);
	}
	
	
	/**
	 * A transformation between APIs for the content of given assets.
	 */
	public <A extends Asset> TypeClause<A> transform(@NonNull Class<A> type) {
		
		return new TypeClause<A>() {
			
			@Override
			public SourceApiClause<A> type(@NonNull AssetType type) {
				
				return new SourceApiClause<A>() {
			
					@Override
					public <S> TargetApiClause<A, S> from(@NonNull Class<S> sourceapi) {
						
						return new TargetApiClause<A,S>() {
							
							@Override
							public <T> TransformClause<A, S, T> to(@NonNull Class<T> targetapi) {
								
								return new TransformClause<A, S, T>() {
									
									@Override
									public Transform<A,S,T> with(@NonNull BiFunction<A, S, T> transform) {
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
											
											/////////////////////////// system support
											@Override
											public String toString() {
												return format("[%s]:%s->%s",type().name(),sourceApi().getSimpleName(),targetApi().getSimpleName());
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
