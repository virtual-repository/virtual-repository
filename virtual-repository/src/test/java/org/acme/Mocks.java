package org.acme;

import static java.util.UUID.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Setter;

import org.mockito.Mockito;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.VirtualBrowser;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@SuppressWarnings("all")
public abstract class Mocks  {
	
	public static ProxyBuilder proxy() {
		return new ProxyBuilder();
	}
	
	public static RepoBuilder repo() {
		return new RepoBuilder();
	}
	
	public static AssetBuilder asset() {
		return new AssetBuilder();
	}
	
	public static AssetType type() {
		return AssetType.of(randomUUID().toString());
	}

	public static VirtualReader readerFor(AssetType type) {
		return readerFor(type,Object.class);
	}

	public static VirtualReader readerFor(AssetType type, Class<?> api) {
		
		VirtualReader importer =  Mockito.mock(VirtualReader.class);
		when(importer.type()).thenReturn(type);
		when(importer.api()).thenReturn(api);
		return importer;
	}
	
	public static VirtualWriter writerFor(AssetType type) {
		return writerFor(type,Object.class);
	}
	
	public static VirtualWriter writerFor(AssetType type, Class<?> api) {
		
		VirtualWriter writer =  Mockito.mock(VirtualWriter.class);
		when(writer.type()).thenReturn(type);
		when(writer.api()).thenReturn(api);
		return writer;
	}
	
	public static class RepoBuilder {
		
		@Setter
		String name = UUID.randomUUID().toString();
		
		@Setter
		VirtualProxy proxy = Mocks.proxy().get();
				
		public Repository get() {
			return new Repository(name, proxy);
		}
		
	}
	
	
	public static class ProxyBuilder {
		
		private VirtualBrowser browser = Mockito.mock(VirtualBrowser.class);
		private List<VirtualReader> readers = new ArrayList<VirtualReader>();
		private  List<VirtualWriter> writers = new ArrayList<VirtualWriter>();
		
		ProxyBuilder with(Accessor ... accessors) {
			
			for (Accessor accessor : accessors)
				if (accessor instanceof VirtualReader)
					readers.add((VirtualReader) accessor);
				else
					writers.add((VirtualWriter) accessor);
			
			return this;
		}
		
		public VirtualProxy get() {
			
			if (readers.isEmpty() && writers.isEmpty()) {
				
				AssetType type = type();
				
				with(readerFor(type),writerFor(type));
			}
			
			VirtualProxy proxy = mock(VirtualProxy.class);
			
			when(proxy.browser()).thenReturn(browser);
			when(proxy.readers()).thenReturn((List) readers);
			when(proxy.writers()).thenReturn((List) writers);
			
			return proxy;
		}
	}

	
	public static class AssetBuilder {

		@Setter
		private String id = UUID.randomUUID().toString();

		private AssetType type = type();
		
		public AssetBuilder of(AssetType type) {
			this.type = type;
			return this;
		}

		public Asset.Private in(Repository repo) {
			
			Asset.Private asset = Mockito.mock(Asset.Private.class);
			when(asset.id()).thenReturn(id);
			when(asset.name()).thenReturn("asset-"+id);
			when(asset.type()).thenReturn(type);
			when(asset.repository()).thenReturn(repo);
			return asset;
		}

	}
}
