package smallgears.virtualrepository.impl;

import static java.lang.String.*;
import static java.util.concurrent.TimeUnit.*;
import static smallgears.api.Apikit.*;
import static smallgears.virtualrepository.common.Constants.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.Repository;
import smallgears.virtualrepository.VirtualRepository;
import smallgears.virtualrepository.VirtualRepository.ContentCheckClause;
import smallgears.virtualrepository.VirtualRepository.PublicationObserver;
import smallgears.virtualrepository.VirtualRepository.PublishModeClause;
import smallgears.virtualrepository.spi.VirtualWriter;

@Slf4j(topic="virtual-repository")
@RequiredArgsConstructor
public class PublicationCompanion {

	@NonNull
	DefaultVirtualRepository vr;
	
	
	ContentCheckClause canPublish(Asset asset) {
		
		return api->writerFor(asset,api).isPresent();

	}
	
	VirtualRepository.PublishWithClause publish(Asset asset) {
	
		return new VirtualRepository.PublishWithClause() {

			@Override
			public PublishModeClause with(Object content) {
				
				return new PublishModeClause() {
					
					Duration timeout = default_publish_timeout;
					

					@Override
					public PublishModeClause timeout(Duration to) {
						timeout=to;
						return this;
					}
					
					@Override
					public void blocking() {
						
						Future<?> future = publish(asset,content);
						
						PublicationObserver dummyObserver = new PublicationObserver(){}; 
						
						_blocking(future,dummyObserver);
						
					}
					
					@Override
					public Future<?> withoutBlocking() {
						return publish(asset,content);
					}
					
					
					@Override
					public void notifying(PublicationObserver observer) {
						
						//propagate submission exceptions synchronously
						Future<?> future = publish(asset,content);
						
						//but then catch results/other exceptions asynchronously
						vr.executor().submit(()->_blocking(future,observer));
						
					}
					
					
					//blocking with notifications
					private void _blocking(Future<?> future, PublicationObserver observer) {
						
						
						try {
							
							future.get(timeout.toMillis(), MILLISECONDS);
							
							observer.onSuccess();
							
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
							
							rethrow(format("cannot publish content for asset %s to repository service %s"
								 			,asset.name()
								 			,asset.repository().name())
								 			,t);
							
						}
					}
				};
				
			}
			
			
		
		};
	
	}
	
	
	private Future<?> publish(Asset asset, Object content) {

		Repository repo = asset.repository();
		
		@SuppressWarnings("all")
		Class<Object> api = (Class) content.getClass();
		
		VirtualWriter<Object> writer = writerFor(asset,api).orElseThrow(
				
				()->new IllegalStateException(format("cannot publis asset %s from %s: no publisher for api %s",asset.id(),repo,api))
		);
		
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					
					long time = System.currentTimeMillis();
					
					log.info("publishing asset {} to {}",asset.name(),asset.repository().name());
					
					writer.publish(asset, content);
					
					log.info("published asset {} to {} in {} ms.",asset.name(),asset.repository().name(),System.currentTimeMillis()-time);
				
				}
				catch (Exception e) {
					
					rethrow(e);
				}
			}
		};
		
		return vr.executor().submit(task);
			

	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private <A> Optional<VirtualWriter<A>> writerFor(Asset asset, Class<A> api) {
		
		if (asset.repository()==null)
			throw new IllegalArgumentException(format("asset %s is not bound to a repository, hence cannot be published.",asset.name()));
		
		List<VirtualWriter<?>> basewriters = asset.repository().proxy().writers();
		
		return vr.transforms().inferWriter(basewriters,asset.type(),api);
	}
}
