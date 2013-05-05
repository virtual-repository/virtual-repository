package org.acme;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.AbstractRepository;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Plugin;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.RepositoryService;

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
	public static final Type<TestAsset> someType = new AbstractType<TestAsset>("some/type") {};
	
	/**
	 * Another pre-defined asset type.
	 */
	public static final Type<TestAsset> anotherType = new AbstractType<TestAsset>("other/type") {};
	
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

	// ////////////////// helpers

	public class TestAsset extends AbstractAsset {

		Object data;
		boolean published = false;

		public TestAsset(String id, AssetType type, Object data) {
			super(type,id, "test-asset-" + id, TestRepo.this);
			this.data = data;
		}

		public Object data() {
			return data;
		}
		
		public void setData(Object data) {
			this.data = data;
		}
	}
	
	public class TestBrowser implements Browser {
		
		@Override
		public Iterable<? extends Asset> discover(List<? extends AssetType> types) {

			List<TestAsset> found = new ArrayList<TestAsset>();
			
			for (TestAsset asset : assets)
				if (types.contains(asset.type()))
					found.add(asset);

			return found;
		}
	}

	abstract class TestAccessor<A> implements Accessor<TestAsset, A> {

		Type<TestAsset> type;
		Class<A> api;

		public TestAccessor(Type<TestAsset> type, Class<A> api) {
			this.type = type;
			this.api = api;
		}

		@Override
		public Class<A> api() {
			return api;
		}

		@Override
		public Type<TestAsset> type() {
			return type;
		}

	}

	class TestWriter<A> extends TestAccessor<A> implements Publisher<TestAsset, A> {

		public TestWriter(Type<TestAsset> type, Class<A> api) {
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

		public TestReader(Type<TestAsset> type, Class<A> api) {
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

		private Type<TestAsset> type = someType;

		public ReaderBuilder boundTo(Type<TestAsset> type) {
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

		private Type<TestAsset> type = someType;

		public WriterBuilder boundTo(Type<TestAsset> type) {
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
		private AssetType type = someType;
		private Object data;
		
		public AssetBuilder id(String id) {
			this.id = id;
			return this;
		}

		public AssetBuilder of(AssetType type) {
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
