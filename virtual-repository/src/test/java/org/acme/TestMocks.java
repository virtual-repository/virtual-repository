package org.acme;

import static java.util.UUID.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.common.Utils;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.VirtualBrowser;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

/**
 * Mocking facilities for testing.
 * @author Fabio Simeoni
 *
 */
@SuppressWarnings("all")
public abstract class TestMocks  {
	
	public static ProxyBuilder aProxy() {
		return new ProxyBuilder();
	}
	
	public static ServiceBuilder aService() {
		return new ServiceBuilder();
	}
	
	public static AssetBuilder anAsset() {
		return new AssetBuilder();
	}
	
	public static AssetType aType() {
		return AssetType.of(randomUUID().toString());
	}

	public static <T extends Asset> VirtualReader<T,Object> aReaderFor(AssetType type) {
		return aReaderFor(type,Object.class);
	}

	public static <T extends Asset, A> VirtualReader<T,A> aReaderFor(AssetType type, Class<A> api) {
		
		VirtualReader importer =  Mockito.mock(VirtualReader.class);
		when(importer.type()).thenReturn(type);
		when(importer.api()).thenReturn(api);
		when(importer.compareTo(any(Accessor.class))).then(
			i->Utils.compareTo(importer, (Accessor) i.getArguments()[0])
		);
		return importer;
	}
	
	public static <T extends Asset> VirtualWriter<T,Object> aWriterFor(AssetType type) {
		return aWriterFor(type,Object.class);
	}
	
	public static <T extends Asset, A> VirtualWriter<T,A> aWriterFor(AssetType type, Class<A> api) {
		VirtualWriter writer =  Mockito.mock(VirtualWriter.class);
		when(writer.type()).thenReturn(type);
		when(writer.api()).thenReturn(api);
		when(writer.compareTo(any(Accessor.class))).then(
				i->Utils.compareTo(writer, (Accessor) i.getArguments()[0])
			);
		return writer;
	}
	
	
	public static class ServiceBuilder {
		
		String name = UUID.randomUUID().toString();
		VirtualProxy proxy = aProxy().get();
		
		public ServiceBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		
		public ServiceBuilder with(VirtualProxy proxy) {
			this.proxy = proxy;
			return this;
		}
		
		public Repository get() {
			return new Repository(name, proxy);
		}
		
		
		
		
	}
	public static class ProxyBuilder {
		
		
		private VirtualBrowser browser = Mockito.mock(VirtualBrowser.class);
		private List<VirtualReader> importers = new ArrayList<VirtualReader>();
		private  List<VirtualWriter> publishers = new ArrayList<VirtualWriter>();
		
		ProxyBuilder with(Accessor ... accessors) {
			
			for (Accessor accessor : accessors)
				if (accessor instanceof VirtualReader)
					importers.add(VirtualReader.class.cast(accessor));
				else
					publishers.add(VirtualWriter.class.cast(accessor));
			
			return this;
		}
		
		public VirtualProxy get() {
			
			if (importers.isEmpty() && publishers.isEmpty()) {
				AssetType type = aType();
				with(aReaderFor(type));
				with(aWriterFor(type));
			}
			
			VirtualProxy proxy = mock(VirtualProxy.class);
			
			when(proxy.browser()).thenReturn(browser);
			when(proxy.readers()).thenReturn((List) importers);
			when(proxy.writers()).thenReturn((List) publishers);
			
			return proxy;
		}
	}

	
	public static class AssetBuilder {

		private String id = UUID.randomUUID().toString();
		private AssetType type = aType();
		
		public AssetBuilder id(String id) {
			this.id = id;
			return this;
		}

		public AssetBuilder of(AssetType type) {
			this.type = type;
			return this;
		}

		public Asset.Private in(Repository service) {
			
			Asset.Private asset = Mockito.mock(Asset.Private.class);
			when(asset.id()).thenReturn(id);
			when(asset.name()).thenReturn("asset-"+id);
			when(asset.type()).thenReturn(type);
			when(asset.repository()).thenReturn(service);
			return asset;
		}

	}
}
