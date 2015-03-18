package org.virtualrepository.impl;

import static java.lang.String.*;
import static java.util.concurrent.TimeUnit.*;
import static org.virtualrepository.common.Constants.*;
import static smallgears.api.Apikit.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.virtualrepository.Asset;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository.ContentCheckClause;
import org.virtualrepository.VirtualRepository.RetrievalObserver;
import org.virtualrepository.VirtualRepository.RetrieveAsClause;
import org.virtualrepository.VirtualRepository.RetrieveModeClause;
import org.virtualrepository.spi.VirtualReader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic="virtual-repository")
@RequiredArgsConstructor
public class RetrievalCompanion {

	@NonNull
	DefaultVirtualRepository vr;

	ContentCheckClause canRetrieve(Asset asset) {
		
		return api->readerFor(asset,api).isPresent();
	}
	
	
	RetrieveAsClause retrieve(@NonNull Asset asset)  {
		
		return new RetrieveAsClause() {
			
			@Override
			public <A> RetrieveModeClause<A> as(Class<A> api) {
	
				return new RetrieveModeClause<A>() {
				 
					 Duration timeout = default_retrieval_timeout;
						
	
					@Override
					public RetrieveModeClause<A> timeout(Duration to) {
						timeout=to;
						return this;
					}
	
					@Override
					public A blocking() {
						
						Future<A> future = retrieve(asset,api);
						
						RetrievalObserver<A> dummyObserver = new RetrievalObserver<A>(){}; 
						
						return _blocking(future,dummyObserver);
					}
	
					@Override
					public Future<A> withoutBlocking() {
						return retrieve(asset,api);
					}
					
					@Override
					public void notifying(RetrievalObserver<A> observer) {
						
						//propagate submission exceptions synchronously
						Future<A> future = retrieve(asset,api);
						
						//but then catch results/other exceptions asynchronously
						vr.executor().submit(()->_blocking(future,observer));
					
					}
					
					//blocking with notifications
					private A _blocking(Future<A> future, RetrievalObserver<A> observer) {
						
						
						try {
							
							A result = future.get(timeout.toMillis(), MILLISECONDS);
							
							observer.onSuccess(result);
							
							return result;
							
						}
						catch(InterruptedException e) {
						
							Thread.currentThread().interrupt();
							
							observer.onError(e);
							
							throw unchecked(e);
							
						}
						catch(TimeoutException | ExecutionException e) {
							
							//step into real cause
							Throwable t =  e instanceof ExecutionException ? e.getCause():e;
							
							observer.onError(t);
							
							throw unchecked(format("cannot retrieve content for asset %s from repository service %s"
										 			,asset.name()
										 			,asset.repository().name())
										 			,t);
						}
					}
					 
					
				};
			};
		
		};
	}
	
	
	private <A> Future<A> retrieve(Asset asset, Class<A> api) {
		
		Repository repo = asset.repository();
		
		VirtualReader<A> reader = readerFor(asset,api).orElseThrow(
		
				()->new IllegalStateException(format("cannot retrieve asset %s from %s: no reader for api %s"
													,asset.name()
													,repo
													,api))
		);
		
		//do it asynchronously to impose timeouts
		
		Callable<A> task = new Callable<A>() {
			
			@Override
			public A call() throws Exception {
				
				log.info("retrieving content of asset {}",asset.name());
				
				long time = System.currentTimeMillis();
				
				A result =  reader.retrieve(asset);
				
				log.info("retrieved content of asset {} in {} ms.",asset.name(),System.currentTimeMillis()-time);
				
				return result;
			}
		};
		
		return vr.executor().submit(task);

	}

	private <A> Optional<VirtualReader<A>> readerFor(Asset asset, Class<A> api) {
		
		if (asset.repository()==null)
			throw new IllegalArgumentException(format("asset %s is not bound to a repository, hence cannot be retrieved.",asset.name()));
		
		List<VirtualReader<?>> basereaders = asset.repository().proxy().readers();
		
		return vr.transforms().inferReader(basereaders,asset.type(),api);
	}
}
