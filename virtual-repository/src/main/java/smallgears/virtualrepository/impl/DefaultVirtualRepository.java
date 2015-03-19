package smallgears.virtualrepository.impl;

import static java.util.stream.Collectors.*;
import static smallgears.virtualrepository.common.Utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;
import smallgears.virtualrepository.Repositories;
import smallgears.virtualrepository.VirtualRepository;

@RequiredArgsConstructor
@Slf4j(topic="virtual-repository")
public class DefaultVirtualRepository implements VirtualRepository {
	
	@NonNull @Getter
	private Repositories repositories;
	
	@NonNull @Getter 
	private Extensions extensions;
	
	private DiscoveryCompanion discoveryCompanion = new DiscoveryCompanion(this);
	private RetrievalCompanion retrievalCompanion = new RetrievalCompanion(this);
	private PublicationCompanion publicationCompanion = new PublicationCompanion(this);

	@Getter
	private Map<String, Asset> assets = new HashMap<String, Asset>();
	
	/**
	 * Replaces the default {@link ExecutorService} used to parallelise and/or time-control discovery, retrieval, and publication. 
	 */
	@Setter @Getter
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	@Override
	public int size() {
		return assets.size();
	}
	
	@Override
	public Iterator<Asset> iterator() {
		
		//defensively isolate from concurrent discoveries
		synchronized (assets) {
			return new ArrayList<>(assets.values()).iterator();
		}
	}
	
	
	@Override
	public Transforms transforms() {
		return extensions.transforms();
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
	public DiscoverClause discover(@NonNull Collection<AssetType> types) {
		
		return discoveryCompanion.discover(types);
	}
	

	@Override
	public ContentCheckClause canRetrieve(Asset asset) {
		
		return retrievalCompanion.canRetrieve(asset);
	}
	
	
	@Override
	public RetrieveAsClause retrieve(@NonNull Asset asset)  {
		
		return retrievalCompanion.retrieve(asset);
	}
	
	
	@Override
	public ContentCheckClause canPublish(Asset asset) {
		
		return publicationCompanion.canPublish(asset);

	}
	
	@Override
	public PublishWithClause publish(Asset asset) {

		return publicationCompanion.publish(asset);

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
}
