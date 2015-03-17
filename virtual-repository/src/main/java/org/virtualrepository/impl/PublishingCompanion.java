package org.virtualrepository.impl;

import static java.lang.String.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.Asset;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository.ContentCheckClause;
import org.virtualrepository.spi.VirtualWriter;

@Slf4j(topic="virtual-repository")
@RequiredArgsConstructor
public class PublishingCompanion {

	@NonNull
	DefaultVirtualRepository vr;
	
	
	
	public ContentCheckClause canPublish(Asset asset) {
		
		return api->writerFor(asset,api).isPresent();

	}
	
	public void publish(final Asset asset, final Object content) {

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
					writer.publish(asset, content);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		try {
			log.info("publishing asset {} to {}",asset.name(),asset.repository().name());
			
			long time = System.currentTimeMillis();
			
			Future<?> future = vr.executor().submit(task);
			
			future.get(3,TimeUnit.MINUTES);
			
			log.info("published asset {} to {} in {} ms.",asset.name(),asset.repository().name(),System.currentTimeMillis()-time);
		}
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		catch(TimeoutException | ExecutionException e) {
			throw new RuntimeException(format("timeout publishing asset %s from repository service %s"
												,asset.name()
												,asset.repository().name()), 
												e instanceof ExecutionException ? e.getCause():e);
		}

	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private <A> Optional<VirtualWriter<A>> writerFor(Asset asset, Class<A> api) {
		
		if (asset.repository()==null)
			throw new IllegalArgumentException(format("asset %s is not bound to a repository, hence cannot be published.",asset.name()));
		
		List<VirtualWriter<?>> writers = asset.repository().writersFor(asset.type());
		
		return vr.extensions().transforms().inferWriter(writers,asset.type(),api);
	}
}
