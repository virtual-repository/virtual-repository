package org.acme;

import static java.util.Arrays.*;
import static java.util.UUID.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.AssetType.*;
import static org.virtualrepository.VR.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Setter;
import lombok.experimental.UtilityClass;

import org.mockito.Mockito;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualBrowser;
import org.virtualrepository.spi.VirtualExtension;
import org.virtualrepository.spi.VirtualProxy;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;

@SuppressWarnings("all")
@UtilityClass
public class Mocks  {
	
	public static Transform<Asset,String,Integer> toNum = 
			transform(Asset.class).type(any).from(String.class).to(Integer.class).with(Integer::valueOf);
	
	public static Transform<Asset,Integer,String> toString = 
			transform(Asset.class).type(any).from(Integer.class).to(String.class).with(String::valueOf);
	
	public ProxyBuilder proxy() {
		return new ProxyBuilder();
	}
	
	public ExtensionBuilder extension() {
		return new ExtensionBuilder();
	}
	
	public RepoBuilder repo() {
		return new RepoBuilder();
	}
	
	public AssetBuilder asset() {
		return new AssetBuilder();
	}
	
	public AssetType type() {
		return AssetType.of(randomUUID().toString());
	}

	public VirtualReader readerFor(AssetType type) {
		return readerFor(type,Object.class);
	}

	public VirtualReader readerFor(AssetType type, Class<?> api) {
		
		VirtualReader importer =  Mockito.mock(VirtualReader.class);
		when(importer.type()).thenReturn(type);
		when(importer.api()).thenReturn(api);
		return importer;
	}
	
	public VirtualWriter writerFor(AssetType type) {
		return writerFor(type,Object.class);
	}
	
	public VirtualWriter writerFor(AssetType type, Class<?> api) {
		
		VirtualWriter writer =  Mockito.mock(VirtualWriter.class);
		when(writer.type()).thenReturn(type);
		when(writer.api()).thenReturn(api);
		return writer;
	}
	
	public class RepoBuilder {
		
		@Setter
		String name = UUID.randomUUID().toString();
		
		@Setter
		VirtualProxy proxy = Mocks.proxy().get();
				
		public Repository get() {
			return new Repository(name, proxy);
		}
		
	}
	
	
	public class ExtensionBuilder {
		
		@Setter
		String name = UUID.randomUUID().toString();
		
		List<Transform<?,?,?>> transforms = new ArrayList<>();
		
		public ExtensionBuilder transforms(Transform<?,?,?> ... transforms) {
			this.transforms.addAll(asList(transforms));
			return this;
		}
				
		public VirtualExtension get() {
			
			return new VirtualExtension() {
				
				@Override
				public Iterable<Transform<?, ?, ?>> transforms() {
					return transforms;
				}
				
				@Override
				public String name() {
					return name;
				}
			};
		}
		
	}
	
	
	public class ProxyBuilder {
		
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

	
	public class AssetBuilder {

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
