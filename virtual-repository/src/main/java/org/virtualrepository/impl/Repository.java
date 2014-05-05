package org.virtualrepository.impl;

import static java.util.Arrays.*;
import static org.virtualrepository.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.MutableAsset;
import org.virtualrepository.spi.Publisher;

/**
 * Default {@link VirtualRepository} implementation.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Repository implements VirtualRepository {

	private static final int DEFAULT_DISCOVERY_TIMEOUT = 30;

	private final static Logger log = LoggerFactory.getLogger(VirtualRepository.class);

	private final Services services;

	private Map<String, Asset> assets = new HashMap<String, Asset>();

	private ExecutorService executor = Executors.newCachedThreadPool();
	
	
	/**
	 * Replaces the default {@link ExecutorService} used to parallelise and/or time-control discovery, retrieval, and publication tasks. 
	 * @param service the service
	 */
	public void setExecutor(ExecutorService service) {
		executor=service;
	}

	/**
	 * Creates an instance over all the {@link RepositoryService}s available on the classpath.
	 * 
	 * @see Services#load()
	 */
	public Repository() {

		services = new Services();
		services.load();

	}

	/**
	 * Creates an instance over given {@link RepositoryService}s.
	 * 
	 * @param services the services
	 */
	public Repository(RepositoryService... services) {
		
		this(new Services(services));
	}

	/**
	 * Creates an instance over a collection of {@link RepositoryService}s
	 * 
	 * @param services the collection
	 */
	public Repository(Services services) {

		notNull("services", services);

		this.services = services;
	}

	@Override
	public Services services() {
		return services;
	}
	
	@Override
	public Collection<RepositoryService> sinks(AssetType... types) {
		
		List<RepositoryService> matching = new ArrayList<RepositoryService>();
		for (RepositoryService service : services) {
			ServiceInspector inspector = new ServiceInspector(service);
			if (!inspector.taken(types).isEmpty())
				matching.add(service);
		}
		return matching;
			
	}
	
	@Override
	public Collection<RepositoryService> sources(AssetType... types) {
		
		List<RepositoryService> matching = new ArrayList<RepositoryService>();
		for (RepositoryService service : this.services) {
			ServiceInspector inspector = new ServiceInspector(service);
			if (!inspector.returned(types).isEmpty())
				matching.add(service);
		}
		return matching;
			
	}

	@Override
	public int discover(AssetType... types) {

		return discover(DEFAULT_DISCOVERY_TIMEOUT,types);
	}
	
	@Override
	public int discover(Iterable<RepositoryService> services, AssetType... types) {
		return discover(DEFAULT_DISCOVERY_TIMEOUT,services, types);
	}
	
	@Override
	public int discover(long timeout,AssetType... types) {

		return discover(timeout,services,types);
	}
	
	
	@Override
	public int discover(long timeout, Iterable<RepositoryService> services, AssetType... types) {
		
		notNull(types);

		final List<AssetType> typeList = asList(types);

		log.info("discovering assets of types {}", typeList);

		CompletionService<Void> completed = new ExecutorCompletionService<Void>(executor);
		
		long time = System.currentTimeMillis();
		
		List<DiscoveryTask> tasks = new ArrayList<DiscoveryTask>();
		
		for (final RepositoryService service : services) {
			
			final ServiceInspector inspector = new ServiceInspector(service);
			
			final Collection<AssetType> importTypes = inspector.returned(types);

			if (importTypes.isEmpty()) {
				log.trace("service {} does not support type(s) {} and will be ignored for discovery",service,typeList);
				continue;
			}
			
			DiscoveryTask task = new DiscoveryTask(service,importTypes);
			completed.submit(task, null);
			tasks.add(task);
		
		}

		//poll results
		for (int i = 0; i < tasks.size(); i++)
			try {
				//wait at most 30 secs for the slowest to finish
				if (completed.poll(timeout, TimeUnit.SECONDS) == null) {
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
		
		//defensive copy to isolate client iterations from concurrent discoveries
		
		synchronized (assets) {
			return new ArrayList<Asset>(assets.values()).iterator();
		}
	}

	@Override
	public Asset lookup(String id) {

		notNull("identifier", id);

		synchronized(this.assets) { //synchronize with concurrent, discovery merges 
			Asset asset = assets.get(id);
			if (asset == null)
				throw new IllegalStateException("unknown asset " + id);
			else
				return asset;
		}

	}
	
	@Override
	public List<Asset> lookup(AssetType type) {
		
		notNull("type", type);
		
		List<Asset> assets = new ArrayList<Asset>();
		
		for (Asset asset : this) //iterating over a copy, see iterator()
			if (asset.type().equals(type))
				assets.add(asset);
		
		return assets;
	}
	
	
	@Override
	public Map<AssetType, List<Asset>> lookup(AssetType... types) {
		
		notNull(types);
		
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
	public <A> A retrieve(final Asset asset, Class<A> api) {

		notNull(asset);
		notNull(api);
		
		if (asset.service()==null)
			throw new IllegalArgumentException("asset "+asset.id()+" has no target service, please set it");

		ServiceInspector inspector = new ServiceInspector(asset.service());

		final Importer<Asset, A> reader = inspector.importerFor(asset.type(), api);

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
		
		final Publisher<Asset, Object> writer = inspector.publisherFor(asset.type(), content.getClass());

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
		
		private final RepositoryService service;
		private final Collection<AssetType> types;
		final Map<String, Asset> discovered = new HashMap<String, Asset>();
		
		DiscoveryTask(RepositoryService service, Collection<AssetType> types) {
			this.service=service;
			this.types=types;
		}
		
		@Override
		public void run() {
			try {
				
				log.info("discovering assets of types {} from {}", types, service.name());
				
				long time = System.currentTimeMillis();
				
				Iterable<? extends MutableAsset> discoveredAssets = service.proxy().browser().discover(types);
				
				int newAssetsByThisTask=0;
				int refreshedAssetsByThisTask=0;
				for (MutableAsset asset : discoveredAssets) {
					if (discovered.put(asset.id(), asset) == null) {
						asset.setService(service);
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
