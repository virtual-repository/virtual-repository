package org.acme;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.AbstractRepository;
import org.fao.virtualrepository.spi.Accessor;
import org.fao.virtualrepository.spi.Browser;
import org.fao.virtualrepository.spi.Importer;
import org.fao.virtualrepository.spi.Plugin;
import org.fao.virtualrepository.spi.Publisher;
import org.fao.virtualrepository.spi.RepositoryService;

/**
 * Simulates a repository service for testing purposes.
 * 
 * <p>
 * The service can build {@link TestAsset}s to add to the repository for staging purposes or to published into it (
 * {@link #asset()}). Assets can be built with the properties that the test requires, the others are defaulted.
 * <p>
 * By default, the repository supports two hypothetical asset types ({@link #someType}, {@link #anotherType}), i.e. has
 * predefined readers and writers which expect content as plain text. <br>
 * More readers and writers can be added to the repository, for the default types or for arbitrary asset types (
 * {@link #addReader()}, {@link #addWriter()}). The readers simply yield remote content by casting it
 * to the bound API. The writers publish by adding the asset to the repository.
 * 
 * 
 * @author Fabio Simeoni
 * 
 */
public class TestRepo extends AbstractRepository implements Plugin {

	/**
	 * A pre-defined asset type.
	 */
	public static final AssetType<TestAsset> someType = new AssetType<TestAsset>() {
		public QName name() {
			return new QName("some/type");
		}

		public String toString() {
			return name().getLocalPart();
		};
	};
	
	/**
	 * Another pre-defined asset type.
	 */
	public static final AssetType<TestAsset> anotherType = new AssetType<TestAsset>() {
		public QName name() {
			return new QName("other/type");
		}

		public String toString() {
			return name().getLocalPart();
		};
	};
	
	@Override
	public List<? extends RepositoryService> services() {
		return Collections.singletonList(this);
	}

	private List<Importer<TestAsset, ?>> readers = new ArrayList<Importer<TestAsset, ?>>();
	private List<Publisher<TestAsset, ?>> writers = new ArrayList<Publisher<TestAsset, ?>>();

	private final List<TestAsset> assets = new ArrayList<TestAsset>();

	/**
	 * Creates a named instance.
	 * 
	 * @param name the name
	 */
	public TestRepo(String name) {
		super(new QName(name));

		// adds string readers for pre-defined types;
		addReader().boundTo(someType).yields(String.class);
		addWriter().boundTo(someType).yields(String.class);
		addReader().boundTo(anotherType).yields(String.class);
		addWriter().boundTo(anotherType).yields(String.class);
	}

	/**
	 * Creates an instance with a random name.
	 * 
	 */
	public TestRepo() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Adds a reader for the default or new asset types.
	 * 
	 * @return a builder for the reader
	 */
	public ReaderBuilder addReader() {
		return new ReaderBuilder();
	}
	
	public void addReader(Importer<TestAsset,?> reader) {
		readers.add(reader);
	}

	/**
	 * Adds a writer for the default or new asset types.
	 * 
	 * @return a builder for the writer
	 */
	public WriterBuilder addWriter() {
		return new WriterBuilder();
	}
	

	/**
	 * Adds an asset to this repository.
	 * 
	 * @return a builder for the asset
	 */
	public AssetBuilder asset() {
		return new AssetBuilder();
	}
	
	@Override
	public Browser browser() {
		return new TestBrowser();
	}

	@Override
	public List<Importer<TestAsset, ?>> importers() {
		return readers;
	}

	@Override
	public List<? extends Publisher<?, ?>> publishers() {
		return writers;
	}

	@Override
	public String toString() {
		return "with assets " + assets.toString();
	}

	// ////////////////// helpers

	public class TestAsset extends AbstractAsset {

		AssetType<TestAsset> type;
		Object data;
		boolean published = false;

		public TestAsset(String id, AssetType<TestAsset> type, Object data) {
			super(id, "test-asset-" + id, TestRepo.this);
			this.type = type;
			this.data = data;
		}

		public Object data() {
			return data;
		}
		
		public void setData(Object data) {
			this.data = data;
		}

		@Override
		public AssetType<TestAsset> type() {
			return type;
		}
	}
	
	public class TestBrowser implements Browser {
		
		@Override
		public Iterable<? extends Asset> discover(List<AssetType<?>> types) {

			List<TestAsset> found = new ArrayList<TestAsset>();
			
			for (TestAsset asset : assets)
				if (types.contains(asset.type()))
					found.add(asset);

			return found;
		}
	}

	abstract class TestAccessor<A> implements Accessor<TestAsset, A> {

		AssetType<TestAsset> type;
		Class<A> api;

		public TestAccessor(AssetType<TestAsset> type, Class<A> api) {
			this.type = type;
			this.api = api;
		}

		@Override
		public Class<A> api() {
			return api;
		}

		@Override
		public AssetType<TestAsset> type() {
			return type;
		}

	}

	class TestWriter<A> extends TestAccessor<A> implements Publisher<TestAsset, A> {

		public TestWriter(AssetType<TestAsset> type, Class<A> api) {
			super(type, api);
		}

		@Override
		public void publish(TestAsset asset, A data) {
			
			//dispatch has worked correctly
			assertEquals(type(), asset.type());
			
			asset.setData(data);
			
			assets.add(asset);

		}
	}

	class TestReader<A> extends TestAccessor<A> implements Importer<TestAsset, A> {

		public TestReader(AssetType<TestAsset> type, Class<A> api) {
			super(type, api);
		}
		
		@Override
		public A retrieve(TestAsset asset) {
			
			//dispatch has worked correctly
			assertEquals(type(), asset.type());
			
			return api.cast(asset.data());
		}

	}

	public class ReaderBuilder {

		private AssetType<TestAsset> type = someType;

		public ReaderBuilder boundTo(AssetType<TestAsset> type) {
			this.type = type;
			return this;
		}

		public <A> Importer<TestAsset, A> yields(Class<A> api) {

			TestReader<A> reader = new TestReader<A>(type, api);
			readers.add(reader);
			return reader;
		}

	}

	public class WriterBuilder {

		private AssetType<TestAsset> type = someType;

		public WriterBuilder boundTo(AssetType<TestAsset> type) {
			this.type = type;
			return this;
		}

		public <A> Publisher<TestAsset, A> yields(Class<A> api) {

			TestWriter<A> writer = new TestWriter<A>(type, api);
			writers.add(writer);
			return writer;
		}

	}

	public class AssetBuilder {

		private String id = UUID.randomUUID().toString();
		private AssetType<TestAsset> type = someType;
		private Object data;
		
		public AssetBuilder id(String id) {
			this.id = id;
			return this;
		}

		public AssetBuilder of(AssetType<TestAsset> type) {
			this.type = type;
			return this;
		}

		public AssetBuilder with(Object data) {
			this.data = data;
			return this;
		}

		public TestAsset get() {

			TestAsset asset = new TestAsset(id, type, data);

			return asset;
		}

		public Asset add() {

			TestAsset asset = get();

			assets.add(asset);

			return asset;
		}

	}
}
