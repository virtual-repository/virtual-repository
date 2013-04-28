package org.acme;

import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.AbstractRepository;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Writer;

/**
 * Simulates a remote repository for testing purposes.
 * 
 * <p>
 * The repository can be staged with hypothetical {@link TestAsset}s ({@link #asset()}). These can be built with the
 * properties that the test requires, the others are defaulted. Properties include the hypothetical remote content.
 * <p>
 * By default, the repository supports two hypothetical asset types ({@link #someType}, {@link #anotherType}), i.e. has
 * pre-defined readers which expect content as plain text. <br>
 * More readers can be added to the repository, for the default or arbitrary asset types ( {@link #addReader()}). The
 * readers have an associated API and simply yield remote content by casting it to that API.
 * 
 * 
 * @author Fabio Simeoni
 * 
 */
public class TestRepo extends AbstractRepository {


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

	private List<TestReader<?>> readers = new ArrayList<TestRepo.TestReader<?>>();

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
		addReader().boundTo(anotherType).yields(String.class);
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
	 * @return a builder for the reader
	 */
	public ReaderBuilder addReader() {
		return new ReaderBuilder();
	}

	/**
	 * Adds an asset to this repository.
	 * @return a builder for the asset
	 */
	public AssetBuilder asset() {
		return new AssetBuilder();
	}
	

	@Override
	public List<TestReader<?>> readers() {
		return readers;
	}

	@Override
	public List<? extends Writer<?, ?>> writers() {
		return emptyList();
	}

	@Override
	public String toString() {
		return "with assets " + assets.toString();
	}

	
	
	//////////////////// helpers
	
	public class TestAsset extends AbstractAsset<TestAsset> {

		AssetType<TestAsset> type;
		Object mockData;
		
		public TestAsset(String id,AssetType<TestAsset> type, Object mockData) {
			super(id,"test-asset-"+id,TestRepo.this);
			this.type=type;
			this.mockData=mockData;
		}
		
		public Object mockData() {
			return mockData;
		}
		
		@Override
		public AssetType<TestAsset> type() {
			return type;
		}
	}

	class TestReader<A> implements Reader<TestAsset, A> {

		AssetType<TestAsset> type;
		Class<A> api;

		public TestReader(AssetType<TestAsset> type, Class<A> api) {
			this.type = type;
			this.api = api;
		}

		@Override
		public AssetType<TestAsset> type() {
			return type;
		}

		@Override
		public Class<A> api() {
			return api;
		}

		@Override
		public Iterable<TestAsset> find() {

			List<TestAsset> found = new ArrayList<TestAsset>();
			for (TestAsset asset : assets)
				if (asset.type().equals(type))
					found.add(asset);

			return found;
		}

		@Override
		public A fetch(TestAsset asset) {
			return api.cast(asset.mockData());
		}

	}

	public class ReaderBuilder {

		private AssetType<TestAsset> type = someType;

		public ReaderBuilder boundTo(AssetType<TestAsset> type) {
			this.type = type;
			return this;
		}

		public <A> Reader<TestAsset, A> yields(Class<A> api) {

			TestReader<A> reader = new TestReader<A>(type, api);
			readers.add(reader);
			return reader;
		}

	}

	public class AssetBuilder {

		private String id = UUID.randomUUID().toString();
		private AssetType<TestAsset> type = someType;
		private Object data;
		private boolean remote = false;

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

		public AssetBuilder withLocal(Object data) {
			this.remote = false;
			return with(data);
		}

		public Asset add() {

			if (data == null)
				data = "content of " + id;

			TestAsset asset = new TestAsset(id,type, data);

			if (!remote)
				asset.setData(data);

			assets.add(asset);

			return asset;
		}

	}
}
