package org.acme;

import static java.util.Collections.*;

import java.util.Arrays;
import java.util.List;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.csv.CSVAsset;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.Writer;

public class TestRepository implements Repository {
	
	private final String name;
	
	private List<? extends Asset> assets;	
	public TestRepository() {
		this("test-repo");
	}
	
	public TestRepository(String name) {
		this(name, new CSVAsset("1", "asset-1"), new CSVAsset("2", "asset-2"), new CSVAsset("3", "asset-3"));

	}
	
	public TestRepository(String name, Asset ... assets) {
		this.name=name;
		this.assets = Arrays.asList(assets);
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public List<? extends Reader> readers() {
		return singletonList(new TestReader());
	}
	
	@Override
	public List<? extends Writer> writers() {
		return emptyList();
	}
	
	@Override
	public String toString() {
		return assets.toString();
	}

	class TestReader implements Reader {
		
		@Override
		public Iterable<? extends Asset> get() {
			return assets;
		}
		
		@Override
		public AssetType<?> type() {
			return new CSV();
		}
	}
}
