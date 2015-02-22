package org.acme;

import static org.mockito.Mockito.*;
import static org.virtualrepository.RepositoryService.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.ServiceProxy;

/**
 * Mocking facilities for testing.
 * @author Fabio Simeoni
 *
 */
@SuppressWarnings("all")
public abstract class TestMocks  {
	
	/**
	 * Creates a mock proxy
	 * @return a mock builder
	 */
	public static ProxyBuilder aProxy() {
		return new ProxyBuilder();
	}
	
	/**
	 * Creates a service
	 * @return a service builder
	 */
	public static ServiceBuilder aService() {
		return new ServiceBuilder();
	}
	
	/**
	 * Creates a mock asset.
	 * @return the mock builder
	 */
	public static AssetBuilder anAsset() {
		return new AssetBuilder();
	}
	
	/**
	 * Creates a mock type for generic asset type
	 * @return the mock type
	 */
	public static AssetType aType() {
		return aTypeFor(Asset.class);
	}
	
	/**
	 * Creates a mock type for a given asset type
	 * @return the mock type
	 */
	public static <T extends Asset> AssetType aTypeFor(Class<T> assetType) {
		return Mockito.mock(AssetType.class);
	}

	/**
	 * Creates an importer for a given asset type and the Object API
	 * @param type the type
	 * @return the mock importer
	 */
	public static <T extends Asset> Importer<T,Object> anImporterFor(AssetType type) {
		return anImporterFor(type,Object.class);
	}

	/**
	 * Creates an importer for a given asset type and API
	 * @param type the type
	 * @param api the API
	 * @return the mock importer
	 */
	public static <T extends Asset, A> Importer<T,A> anImporterFor(AssetType type, Class<A> api) {
		Importer importer =  Mockito.mock(Importer.class);
		when(importer.type()).thenReturn(type);
		when(importer.api()).thenReturn(api);
		return importer;
	}
	
	/**
	 * Creates a publisher for a given asset type and the Object API
	 * @param type the type
	 * @return the mock importer
	 */
	public static <T extends Asset> Publisher<T,Object> aPublisherFor(AssetType type) {
		return aPublisherFor(type,Object.class);
	}
	
	/**
	 * Creates a mock importer for a given asset type and API
	 * @param type the type
	 * @param api the API
	 * @return the mock importer
	 */
	public static <T extends Asset, A> Publisher<T,A> aPublisherFor(AssetType type, Class<A> api) {
		Publisher publisher =  Mockito.mock(Publisher.class);
		when(publisher.type()).thenReturn(type);
		when(publisher.api()).thenReturn(api);
		return publisher;
	}
	
	
	public static class ServiceBuilder {
		
		String name = UUID.randomUUID().toString();
		ServiceProxy proxy = aProxy().get();
		
		/**
		 * Set a name for the service
		 * @param name the identifier
		 * @return this builder
		 */
		public ServiceBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		
		/**
		 * Sets a proxy for the service
		 * @param name the identifier
		 * @return this builder
		 */
		public ServiceBuilder with(ServiceProxy proxy) {
			this.proxy = proxy;
			return this;
		}
		
		public RepositoryService get() {
			return service(name, proxy);
		}
		
		
		
		
	}
	public static class ProxyBuilder {
		
		
		private Browser browser = Mockito.mock(Browser.class);
		private List<Importer> importers = new ArrayList<Importer>();
		private  List<Publisher> publishers = new ArrayList<Publisher>();
		
		/**
		 * Adds accessors to the mock service
		 * @param accessors the accessors
		 * @return this builder
		 */
		ProxyBuilder with(Accessor ... accessors) {
			
			for (Accessor accessor : accessors)
				if (accessor instanceof Importer)
					importers.add(Importer.class.cast(accessor));
				else
					publishers.add(Publisher.class.cast(accessor));
			
			return this;
		}
		
		/**
		 * Returns the mock service with a random name
		 * @return the mock service
		 */
		public ServiceProxy get() {
			
			if (importers.isEmpty() && publishers.isEmpty()) {
				AssetType type = aType();
				with(anImporterFor(type));
				with(aPublisherFor(type));
			}
			
			ServiceProxy proxy = mock(ServiceProxy.class);
			
			when(proxy.browser()).thenReturn(browser);
			when(proxy.importers()).thenReturn((List) importers);
			when(proxy.publishers()).thenReturn((List) publishers);
			
			return proxy;
		}
	}

	
	public static class AssetBuilder {

		private String id = UUID.randomUUID().toString();
		private AssetType type = aType();
		
		/**
		 * Set an identifier for the mock asset
		 * @param name the identifier
		 * @return this builder
		 */
		public AssetBuilder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * Sets a type for the mock asset
		 * @param type the type
		 * @return this builder
		 */
		public AssetBuilder of(AssetType type) {
			this.type = type;
			return this;
		}

		/**
		 * Sets a service for the mock asset
		 * @param service the service
		 * @return the mock asset
		 */
		public Asset.Private in(RepositoryService service) {
			
			Asset.Private asset = Mockito.mock(Asset.Private.class);
			when(asset.id()).thenReturn(id);
			when(asset.name()).thenReturn("asset-"+id);
			when(asset.type()).thenReturn(type);
			when(asset.service()).thenReturn(service);
			return asset;
		}

	}
}
