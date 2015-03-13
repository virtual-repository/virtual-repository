package org.virtualrepository.impl;

import static java.lang.String.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.*;
import static org.virtualrepository.common.Constants.*;
import static org.virtualrepository.common.Utils.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repositories;
import org.virtualrepository.Repository;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@RequiredArgsConstructor
@Slf4j(topic="virtual-repository")
public class DefaultVirtualRepository implements VirtualRepository {
	
	

	@NonNull @Getter
	private Repositories repositories;
	
	@NonNull @Getter 
	private Extensions extensions;

	private Map<String, Asset> assets = new HashMap<String, Asset>();
	
	/**
	 * Replaces the default {@link ExecutorService} used to parallelise and/or time-control discovery, retrieval, and publication. 
	 */
	@Setter
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	@Override
	public int size() {
		return assets.size();
	}
	
	@Override
	public DiscoverClause discover(@NonNull Collection<AssetType> types) {
		
		return new DiscoverClause() {
			
			Duration timeout = default_discovery_timeout;
			Repositories repos = repositories;
			
			@Override
			public DiscoverClause timeout(Duration to) {
				timeout=to;
				return this;
			}
			
			
			
			@Override
			public DiscoverClause over(Repositories repositories) {
				repos = repositories;
				return this;
			}
			
			@Override
			public int blocking() {
				return discover(timeout, repos, types, new Observer(){});
			}
			
			@Override
			public Future<Integer> withoutBlocking() {
				return executor.submit(()->blocking());
			}
			
			@Override
			public void notifying(@NonNull Observer observer) {
				
				discover(timeout, repos, types, observer);
			}
		};
	}
	
	private int discover(Duration timeout, @NonNull Iterable<Repository> repositories, Collection<AssetType> types, Observer observer) {
		
		CompletionService<Collection<Asset>> service = new ExecutorCompletionService<Collection<Asset>>(executor);

		log.info("discovering assets of types {}", types);

		long time = currentTimeMillis();
		
		//produce
		
		List<DiscoveryTask> submitted = new ArrayList<DiscoveryTask>();
		
		for (Repository repo : repositories) {
			
			List<AssetType> disseminated = repo.disseminated(types);

			if (!disseminated.isEmpty()) {

				DiscoveryTask task = new DiscoveryTask(repo,disseminated);
				
				service.submit(task);

				submitted.add(task);
			}
		}

		//consume
		
		int completed = 0;
		int news =0;
		int refreshed=0;

		for (DiscoveryTask task : submitted)
			
			try {
				
				Future<Collection<Asset>> nextResults = service.poll(timeout.toMillis(), MILLISECONDS);
				
				if (nextResults == null) {
					log.warn("asset discovery timed out after succesful interaction with {} service(s)", completed);
					break;
				}
				
				synchronized (assets) { //synchronize with concurrent merges/iterations
					
					
					for (Asset a : nextResults.get())
						if (assets.put(a.id(),a)== null)
							news++;
						else 
							refreshed++;	
				}
				

				observer.onNext(nextResults.get());
				
				completed++;
				
			}
			catch(InterruptedException e) {
				
				Thread.currentThread().interrupt(); // be a good citizen
				log.warn("asset discovery was interrupted after succesful interaction with {} service(s)", completed);
				break;
			
			}
			catch (ExecutionException e) {
				
				log.warn("cannot discover assets from " + task.repo.name(), e.getCause());
				
			}
		
		observer.onCompleted();
	    


		log.info("discovered {} new asset(s) of type(s) {} (refreshed {}, total {}) in {} ms.", news, types, refreshed,
				assets.size(),System.currentTimeMillis()-time);

		return news;
	}

	@Override
	public Iterator<Asset> iterator() {
		
		//defensively isolate from concurrent discoveries
		synchronized (assets) {
			return new ArrayList<>(assets.values()).iterator();
		}
	}

	@Override
	public Optional<Asset> lookup(@NonNull String id) {

		synchronized (this.assets) { //synchronize with concurrent, discovery merges 
			return Optional.ofNullable(assets.get(id));
		}

	}
	
	@Override
	public List<Asset> lookup(@NonNull AssetType type) {
		
		return stream().filter(a->ordered(a.type(),type)).collect(toList());
		
	}
	
	
	@Override
	public Map<AssetType, List<Asset>> lookup(@NonNull AssetType... types) {
		
		Map<AssetType,List<Asset>> assets = new HashMap<AssetType, List<Asset>>();
		for (AssetType type : types)
			assets.put(type,new ArrayList<Asset>());
		
		for (Asset asset : this) { //iterating over a copy, see iterator()
			List<Asset> assetsByType = assets.get(asset.type());
			if (assetsByType!=null)
				assetsByType.add(asset);
			
		}
		
		return assets;
	}
	
	@Override
	public boolean canRetrieve(Asset asset, Class<?> api) {
		
		return readerFor(asset,api).isPresent();
	}
	
	@Override
	public boolean canPublish(Asset asset, Class<?> api) {
		
		return writerFor(asset,api).isPresent();

	}

	@Override
	public <A> A retrieve(@NonNull Asset asset, @NonNull Class<A> api) {
		
		Repository repo = asset.repository();
		
		VirtualReader<A> reader = readerFor(asset,api).orElseThrow(
		
				()->new IllegalStateException(format("cannot retrieve asset %s from %s: no reader for api %s",asset.id(),repo,api))
		);
		
		Callable<A> task = new Callable<A>() {
			
			@Override
			public A call() throws Exception {
				return reader.retrieve(asset);
			}
		};
		
		try {
			log.info("retrieving data for asset {} ({})",asset.id(),asset.name());
			
			long time = System.currentTimeMillis();
			
			Future<A> future = executor.submit(task);
			
			A result = future.get(3,TimeUnit.MINUTES);
			
			log.info("retrieved data for asset {} ({}) in {} ms.",asset.id(),asset.name(),System.currentTimeMillis()-time);
			
			return result;
		}
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		catch(TimeoutException e) {
			throw new RuntimeException("timeout retrieving content for asset \n" + asset + "\n from repository service "
					+ asset.repository().name(), e);
		}
		catch (ExecutionException e) {
			throw new RuntimeException("error retrieving content for asset \n" + asset + "\n from repository service "
					+ asset.repository().name(), e.getCause());
		}

	}

	@Override
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
			Future<?> future = executor.submit(task);
			future.get(3,TimeUnit.MINUTES);
			log.info("published asset {} to {} in {} ms.",asset.name(),asset.repository().name(),System.currentTimeMillis()-time);
		}
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		catch(TimeoutException e) {
			throw new RuntimeException("timeout publishing asset \n" + asset + "\n from repository service "
					+ asset.repository().name(), e);
		}
		catch (ExecutionException e) {
			throw new RuntimeException("error publishing asset \n" + asset + "\n through repository service "
					+ asset.repository().name(), e.getCause());
		}

	}

	@RequiredArgsConstructor
	private class DiscoveryTask implements Callable<Collection<Asset>> {
		
		@NonNull
		final Repository repo;
		
		@NonNull
		Collection<AssetType> types;
		
		@Override
		public Collection<Asset> call() {
			
			try {
				
				log.info("discovering assets of types {} from {}", types, repo.name());
				
				long time = System.currentTimeMillis();
				
				Collection<Asset> discovered = repo.proxy().browser().discover(types);
				
				log.info("discovered {} asset(s) of types {} from {} in {} ms. ",  discovered.size(), types, repo.name(), System.currentTimeMillis()-time);
				
				return discovered;
				
			} catch (Exception e) {
				
				log.warn("cannot discover assets from " + repo.name(), e);
			
				return emptyList();
			}
						

		}
	}
	
	@Override
	public void shutdown() {
		
		try {
			
			log.info("shutting down...");
			
			executor.shutdown();
			executor.awaitTermination(3000, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e) {
			log.warn("no clean shutdown (see cause)",e);
		}
		
		repositories.shutdown();
		extensions.shutdown();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private <A> Optional<VirtualReader<A>> readerFor(Asset asset, Class<A> api) {
		
		if (asset.repository()==null)
			throw new IllegalArgumentException("asset "+asset.id()+" is not bound to a repository, hence cannot be retrieved.");
		
		List<VirtualReader<?>> readers = asset.repository().readersFor(asset.type());
		
		return extensions.transforms().inferReader(readers,asset.type(),api);
	}
	
	private <A> Optional<VirtualWriter<A>> writerFor(Asset asset, Class<A> api) {
		
		if (asset.repository()==null)
			throw new IllegalArgumentException("asset "+asset.id()+" is not bound to a repository, hence cannot be published.");
		
		List<VirtualWriter<?>> writers = asset.repository().writersFor(asset.type());
		
		return extensions.transforms().inferWriter(writers,asset.type(),api);
	}
}
