package org.virtualrepository.impl;

import static java.lang.System.*;
import static java.util.Arrays.*;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.*;
import static org.virtualrepository.Types.*;
import static org.virtualrepository.common.Constants.*;

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
import org.virtualrepository.VR;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@RequiredArgsConstructor
@Slf4j(topic="virtual-repository")
public class DefaultVirtualRepository implements VirtualRepository {

	@NonNull @Getter
	private Repositories repositories;

	private Map<String, Asset> assets = new HashMap<String, Asset>();

	
	@Setter
	/**
	 * Replaces the default {@link ExecutorService} used to parallelise and/or time-control discovery, retrieval, and publication. 
	 */
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	
	@Override
	public DiscoverClause discover(AssetType... types) {
		
		return new DiscoverClause() {
			
			long timeout = default_discovery_timeout;
			Repositories repos = repositories;
			
			@Override
			public DiscoverClause timeout(long to) {
				timeout=to;
				return this;
			}
			
			@Override
			public DiscoverClause over(Repository... repositories) {
				repos = VR.repositories(repositories);
				return this;
			}
			
			@Override
			public DiscoverClause over(Repositories repositories) {
				repos = repositories;
				return this;
			}
			
			@Override
			public int now() {
				return discover(timeout, repos, types);
			}
		};
	}
	
	private int discover(long timeout, @NonNull Iterable<Repository> repositories, @NonNull AssetType... types) {
		
		final List<AssetType> typeList = asList(types);

		log.info("discovering assets of types {}", typeList);

		CompletionService<Void> completed = new ExecutorCompletionService<Void>(executor);
		
		long time = currentTimeMillis();
		
		List<DiscoveryTask> tasks = new ArrayList<DiscoveryTask>();
		
		for (Repository repo : repositories) {
			
			final List<AssetType> importTypes = repo.returned(types);

			if (importTypes.isEmpty())
				log.trace("service {} does not support type(s) {} and will be ignored for discovery",repo,typeList);
			
			else {

				DiscoveryTask task = new DiscoveryTask(repo,importTypes);
				
				completed.submit(task, null);

				tasks.add(task);

			}
			
		
		}

		//poll results
		for (int i = 0; i < tasks.size(); i++)
			
			try {
				//wait at most the timeout for the slowest to finish
				if (completed.poll(timeout, SECONDS) == null) {
					log.warn("asset discovery timed out after succesful interaction with {} service(s)", i);
					break;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // be a good citizen
				log.warn("asset discovery was interrupted after succesful interaction with {} service(s)", i);
			}

		//merge results
		int news =0;
		int refreshed=0;
		
		synchronized (assets) { //synchronize with concurrent merges
			for (DiscoveryTask task : tasks)
				for (Map.Entry<String,Asset> e : task.discovered.entrySet())
					if (assets.put(e.getKey(),e.getValue())== null)
						news++;
					else 
						refreshed++;
			
		}
		
		log.info("discovered {} new asset(s) of type(s) {} (refreshed {}, total {}) in {} ms.", news, typeList, refreshed,
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
		
		return stream().filter(a->type==any || a.type().equals(type)).collect(toList());
		
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
	
	public boolean canRetrieve(Asset asset, Class<?> api) {
		
		if (asset.service()==null)
			throw new IllegalArgumentException("asset "+asset.id()+" has no target service and cannot be retrieved.");
		
		return new ServiceInspector(asset.service()).takes(asset.type(), api);
		
	}

	@Override
	public <A> A retrieve(@NonNull Asset asset, @NonNull Class<A> api) {
		
		if (asset.service()==null)
			throw new IllegalArgumentException("asset "+asset.id()+" has no target service and cannot be retrieved");

		ServiceInspector inspector = new ServiceInspector(asset.service());

		final VirtualReader<Asset, A> reader = inspector.importerFor(asset.type(), api);

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
					+ asset.service().name(), e);
		}
		catch (ExecutionException e) {
			throw new RuntimeException("error retrieving content for asset \n" + asset + "\n from repository service "
					+ asset.service().name(), e.getCause());
		}

	}

	@Override
	public void publish(final Asset asset, final Object content) {

		if (asset.service()==null)
			throw new IllegalArgumentException("asset has no target service, please set it");
		
		ServiceInspector inspector = new ServiceInspector(asset.service());
		
		final VirtualWriter<Asset, Object> writer = inspector.publisherFor(asset.type(), content.getClass());

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
			log.info("publishing asset {} to {}",asset.name(),asset.service().name());
			long time = System.currentTimeMillis();
			Future<?> future = executor.submit(task);
			future.get(3,TimeUnit.MINUTES);
			log.info("published asset {} to {} in {} ms.",asset.name(),asset.service().name(),System.currentTimeMillis()-time);
		}
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		catch(TimeoutException e) {
			throw new RuntimeException("timeout publishing asset \n" + asset + "\n from repository service "
					+ asset.service().name(), e);
		}
		catch (ExecutionException e) {
			throw new RuntimeException("error publishing asset \n" + asset + "\n through repository service "
					+ asset.service().name(), e.getCause());
		}

	}

	
	private class DiscoveryTask implements Runnable {
		
		private final Repository service;
		private final Collection<AssetType> types;
		final Map<String, Asset> discovered = new HashMap<String, Asset>();
		
		DiscoveryTask(Repository service, Collection<AssetType> types) {
			this.service=service;
			this.types=types;
		}
		
		@Override
		public void run() {
			try {
				
				log.info("discovering assets of types {} from {}", types, service.name());
				
				long time = System.currentTimeMillis();
				
				Iterable<? extends Asset.Private> discoveredAssets = service.proxy().browser().discover(types);
				
				int newAssetsByThisTask=0;
				int refreshedAssetsByThisTask=0;
				for (Asset.Private asset : discoveredAssets) {
					if (discovered.put(asset.id(), asset) == null) {
						asset.service(service);
						newAssetsByThisTask++;
					}
					else
						refreshedAssetsByThisTask++;
				}
				
				log.info("discovered {} asset(s) of types {} ({} new) from {} in {} ms. ",  newAssetsByThisTask+refreshedAssetsByThisTask, types, newAssetsByThisTask, service.name(), System.currentTimeMillis()-time);
				
			} catch (Exception e) {
				log.warn("cannot discover assets from repository service " + service.name(), e);
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
			log.warn("cannot shutdown this hub",e);
		}
		
	}
}
