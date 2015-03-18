package org.acme;

import static java.lang.String.*;
import static java.util.Arrays.*;
import static java.util.UUID.*;
import static java.util.stream.Collectors.*;
import static org.acme.Mocks.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.AssetType.*;
import static org.virtualrepository.VR.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.AssetType.Simple;
import org.virtualrepository.VR.AssetClause;
import org.virtualrepository.Repository;
import org.virtualrepository.VR;
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
	
	static {
		
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
	}
	
	public static Transform<String,Integer> toNum = transform(any).from(String.class).to(Integer.class).with(s->Integer.valueOf(s));
	
	public static Transform<Integer,String> toString = transform(any).from(Integer.class).to(String.class).with(s->String.valueOf(s));
	
	public ProxyBuilder proxy() {
		return new ProxyBuilder();
	}
	
	public ExtensionBuilder extension() {
		return new ExtensionBuilder();
	}
	
	public RepoBuilder repo() {
		return new RepoBuilder();
	}
	
	public VR.AssetClause testAsset() {

		String id = UUID.randomUUID().toString();
		
		return asset(id).name(format("asset-%s",id));
		
	
	}
	
	public Simple type() {
		return AssetType.of(randomUUID().toString());
	}

	public VirtualReader readerFor(AssetType type) {
		return readerFor(type,Object.class);
	}

	public <T> VirtualReader<T> readerFor(AssetType type, Class<T> api) {
		
		VirtualReader importer =  Mockito.mock(VirtualReader.class);
		when(importer.type()).thenReturn(type);
		when(importer.api()).thenReturn(api);
		return importer;
	}
	
	public <T> VirtualReader<T> readerFor(Class<T> api) {
		
		return readerFor(any,api);
	}
	
	public VirtualReader reader() {
		
		return readerFor(any);
	}
	
	public VirtualWriter writerFor(AssetType type) {
		return writerFor(type,Object.class);
	}
	
	public <T> VirtualWriter<T> writerFor(AssetType type, Class<T> api) {
		
		VirtualWriter writer =  Mockito.mock(VirtualWriter.class);
		when(writer.type()).thenReturn(type);
		when(writer.api()).thenReturn(api);
		return writer;
	}
	
	public <T> VirtualWriter<T> writerFor(Class<T> api) {
		
		return writerFor(any,api);
	}
	
	public <T> VirtualWriter<T> writer() {
		
		return writerFor(any);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	AssetType some_type = type();
	AssetType some_other_type = type();

	public Repository repoThatReadsSomeType() {
		
		return repo().with(proxy().with(readerFor(some_type))).get();
	}
	
	@SneakyThrows @SuppressWarnings("all")
	public Repository repoThatReadsSomeTypeAndContains(Object ... contents) {
		
		VirtualReader reader = readerFor(some_type,contents[0].getClass());
		
		Repository repository = repo().with(proxy().with(reader)).get();
		
		for (Object content : contents)
			when(reader.retrieve(any(Asset.class))).thenReturn(content);
		
		return repository;
	}
	
	public Repository repoThatReadsSomeOtherType() {
		
		return repo().with(proxy().with(readerFor(some_other_type))).get();
	}
	
	@SneakyThrows
	public Repository repoThatTakesSomeTypeAnd(Class<?> ... apis) {
		
		Map<Asset,Object> assets = new HashMap<Asset,Object>();
		
		VirtualWriter<?>[] writers = new VirtualWriter<?>[apis.length];
		
		for (int i =0; i <apis.length; i++) {
			
			VirtualWriter<?> writer = writerFor(some_type,apis[i]);
			
			doAnswer(call-> {
				assets.put(call.getArgumentAt(0,Asset.class),call.getArgumentAt(1,Object.class));
				return null;
			
			}).when(writer).publish(any(Asset.class),anyObject());
					
			writers[i]=writer;
			
		}
		
		VirtualReader<?> reader = readerFor(some_type,Object.class);
		
		when(reader.retrieve(any(Asset.class))).then(call-> assets.get(call.getArgumentAt(0,Asset.class)));
		
		return repo().with(proxy().with(writers).with(reader)).get();
	}
	
	public AssetClause assetOfSomeType() {
		
		return testAsset().of(some_type);
	}
	
	public AssetClause assetOfSomeOtherType() {
		
		return testAsset().of(some_other_type);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class RepoBuilder {
		
		@Setter
		String name = UUID.randomUUID().toString();
		
		VirtualProxy proxy = Mocks.proxy().get();
				
		public Repository get() {
			return new Repository(name, proxy);
		}
		
		public RepoBuilder with(VirtualProxy proxy) {
			this.proxy=proxy;
			return this;
		}
		
		public RepoBuilder with(ProxyBuilder builder) {
			return with(builder.get());
		}
		
	}
	
	
	public class ExtensionBuilder {
		
		@Setter
		String name = UUID.randomUUID().toString();
		
		List<Transform<?,?>> transforms = new ArrayList<>();
		
		public ExtensionBuilder transforms(Transform<?,?> ... transforms) {
			this.transforms.addAll(asList(transforms));
			return this;
		}
				
		public VirtualExtension get() {
			
			return new VirtualExtension() {
				
				@Override
				public Iterable<Transform<?, ?>> transforms() {
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

}
