package org.virtualrepository;

import static java.lang.String.*;
import static java.util.UUID.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
	public Transforms transforms(Transform<?,?> ... transforms) {
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
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public AssetNameClause asset() {
		
		return asset(randomUUID().toString());
	}

	public AssetNameClause asset(@NonNull String id) {
		
		@RequiredArgsConstructor
		class Clause implements AssetClause {
			
			@NonNull
			String name;

			AssetType type = AssetType.any;
			
			@Override
			public AssetClause of(@NonNull AssetType type) {
				this.type = type;
				return this;
			}

			@Override
			public Asset in(@NonNull Repository repo) {
				return new Asset(type, id, name, repo);
			}

			@Override
			public Asset justDiscovered() {
				return new Asset(type, id, name);
			}
			
		}
		
		return Clause::new; 

	}
	
	
	public interface AssetNameClause {
		
		/**
		 * The name of the asset.
		 */
		AssetClause name(String name);
		
		
	}

	public interface AssetClause {
		
		/**
		 * The type of the asset.
		 */
		AssetClause of(AssetType type);
		
		/**
		 * The type of the asset.
		 * <p>
		 * Creates a simple type with a given name
		 */
		default AssetClause of(String type) {
			return of(AssetType.of(type));
		}
		
		/**
		 * The repository where the asset is to be published.
		 */
		Asset in(Repository repo);

		/**
		 * Returns the asset after discovery.
		 */
		Asset justDiscovered();
		
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A transformation between APIs for the content of given assets.
	 */
	public <A extends Asset> SourceApiClause transform(@NonNull AssetType type) {
		
			return new SourceApiClause() {
			
				@Override
				public <S> TargetApiClause<S> from(@NonNull Class<S> sourceapi) {
					
					return new TargetApiClause<S>() {
						
						@Override
						public <T> TransformClause<S, T> to(@NonNull Class<T> targetapi) {
							
							return new TransformClause<S, T>() {
								
								@Override
								public Transform<S,T> with(@NonNull BiFunction<Asset, S, T> transform) {
									return  new Transform<S,T>() {
	
										@Override
										public T apply(Asset asset, S input)throws Exception {
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
											return format("%s:%s-to-%s",type().name(),sourceApi().getSimpleName(),targetApi().getSimpleName());
										}
										
										
									};
								}
							};
						}
					};
				}
			};
	}

	public interface SourceApiClause {
		
		/**
		 * The API to transform.
		 */
		<S> TargetApiClause<S> from(Class<S> sourceapi);

	}
	
	public interface TargetApiClause<S> {
		
		/**
		 * The transformed API.
		 */
		<T> TransformClause<S,T> to(Class<T> targetapi);

	}
	
	public interface TransformClause<S,T> {
		
		/**
		 * The transformation (asset dependent).
		 */
		Transform<S,T> with(BiFunction<Asset,S,T> transform);
		
		/**
		 * The transformation (asset independent).
		 */
		default Transform<S,T> with(Function<S,T> transform) {
			return with((__,stream)->transform.apply(stream));
		}

	}
}
