package org.fao.virtualrepository.impl;

import static java.util.Arrays.*;
import static org.fao.virtualrepository.Utils.*;

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
import java.util.concurrent.atomic.AtomicInteger;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.VirtualRepository;
import org.fao.virtualrepository.spi.Importer;
import org.fao.virtualrepository.spi.Publisher;
import org.fao.virtualrepository.spi.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link VirtualRepository} implementation.
 * 
 * @author Fabio Simeoni
 * 
 */
public class DefaultVirtualRepository implements VirtualRepository {

	private final static Logger log = LoggerFactory.getLogger(VirtualRepository.class);

	private final Repositories repositories;

	private Map<String, Asset> assets = new HashMap<String, Asset>();

	private final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * Creates an instance over all the repository services that can be discovered on the classpath.
	 * 
	 * @see Repositories#load()
	 */
	public DefaultVirtualRepository() {

		repositories = new Repositories();
		repositories.load();

	}

	/**
	 * Creates an instance over given repository services
	 * 
	 * @param repositories the repository services
	 */
	public DefaultVirtualRepository(RepositoryService... repositories) {

		this.repositories = new Repositories();
		this.repositories.add(repositories);
	}

	/**
	 * Creates an instance over given repository services
	 * 
	 * @param repositories the repository services
	 */
	public DefaultVirtualRepository(Repositories repositories) {

		notNull("repositories", repositories);

		this.repositories = repositories;
	}

	@Override
	public Repositories repositories() {

		return repositories;
	}

	@Override
	public int discover(AssetType<?>... types) {

		notNull(types);

		final List<AssetType<?>> typeList = asList(types);

		log.info("discovering assets of types {}", typeList);

		final AtomicInteger discovered = new AtomicInteger(0);
		final AtomicInteger refreshed = new AtomicInteger(0);

		CompletionService<Void> completed = new ExecutorCompletionService<Void>(executor);

		for (final RepositoryService repository : repositories) {

			Runnable task = new Runnable() {

				@Override
				public void run() {
					try {
						for (Asset asset : repository.browser().discover(typeList))
							if (assets.put(asset.id(), asset) == null)
								discovered.incrementAndGet();
							else
								refreshed.incrementAndGet();
					} catch (Exception e) {
						log.warn("cannot discover assets from repository service " + repository.name(), e);
					}

				}
			};

			completed.submit(task, null);

		}

		for (int i = 0; i < repositories.size(); i++)
			try {
				//wait at most 30 secs for the slowest to finish
				if (completed.poll(30, TimeUnit.SECONDS) == null)
					log.warn("asset discovery timed out after succesful interaction with {} service(s)", i);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // be a good citizen
				log.warn("asset discovery was interrupted after succesful interaction with {} service(s)", i);
			}

		log.info("discovered {} new assets of types {}, refreshed {}, total {}", discovered, typeList, refreshed,
				assets.size());

		return discovered.get();
	}

	@Override
	public Iterator<Asset> iterator() {
		return assets.values().iterator();
	}

	@Override
	public Asset lookup(String id) {

		notNull("identifier", id);

		Asset asset = assets.get(id);

		if (asset == null)
			throw new IllegalStateException("unknown asset " + id);
		else
			return asset;

	}

	@Override
	public <A> A retrieve(final Asset asset, Class<A> api) {

		notNull(asset);
		notNull(api);

		RepositoryManager manager = new RepositoryManager(asset.repository());

		final Importer<Asset, A> reader = manager.reader(asset, api);

		Callable<A> task = new Callable<A>() {
			
			@Override
			public A call() throws Exception {
				return reader.retrieve(asset);
			}
		};
		
		try {
			log.info("retrieving data for asset {} ({})",asset.id(),asset.name());
			Future<A> result = executor.submit(task);
			return result.get(1,TimeUnit.MINUTES);
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

		RepositoryManager manager = new RepositoryManager(asset.repository());

		final Publisher<Asset, Object> writer = manager.writer(asset, content.getClass());

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
			
			log.info("publish for asset {} ({})",asset.id(),asset.name());
			Future<?> result = executor.submit(task);
			result.get(1,TimeUnit.MINUTES);
			
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

}
