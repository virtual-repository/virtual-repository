package smallgears.virtualrepository.impl;

import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.concurrent.TimeUnit.*;
import static smallgears.virtualrepository.common.Constants.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;
import smallgears.virtualrepository.Repositories;
import smallgears.virtualrepository.Repository;
import smallgears.virtualrepository.VirtualRepository.DiscoverClause;
import smallgears.virtualrepository.VirtualRepository.DiscoveryObserver;

@Slf4j(topic="virtual-repository")
@RequiredArgsConstructor
public class DiscoveryCompanion {

	@NonNull
	DefaultVirtualRepository vr;
	
	public DiscoverClause discover(@NonNull Collection<AssetType> types) {
		
		return new DiscoverClause() {
			
			Duration timeout = default_discovery_timeout;
			
			Repositories repos = vr.repositories();
			
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
				
				DiscoveryObserver dummyObserver = new DiscoveryObserver() {};
				
				return discover(timeout, repos, types, dummyObserver);
			}
			
			@Override
			public Future<Integer> withoutBlocking() {
				
				return vr.executor().submit(()->blocking());
			}
			
			@Override
			public void notifying(@NonNull DiscoveryObserver observer) {
				
				vr.executor().submit(()->discover(timeout, repos, types, observer));
				
			}
		};
	}
	
	private int discover(Duration timeout, @NonNull Iterable<Repository> repositories, Collection<AssetType> types, DiscoveryObserver observer) {
		
		CompletionService<Collection<Asset>> service = new ExecutorCompletionService<Collection<Asset>>(vr.executor());

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
				
				synchronized (vr.assets()) { //synchronize with concurrent merges/iterations
					
					
					for (Asset a : nextResults.get()) {
					
						a.repository(task.repo);
						
						if (vr.assets().put(a.id(),a)== null) {
							news++;
							try {
								observer.onNext(a);
							}
							catch(Throwable ignoreObserverIssue) {}
						}
						else 
							refreshed++;	
					}
				}
				
				
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
				vr.size(),System.currentTimeMillis()-time);

		return news;
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
}
