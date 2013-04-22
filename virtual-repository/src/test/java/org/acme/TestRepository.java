package org.acme;

import static java.util.Collections.*;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.csv.CSVAsset;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.RepositoryDescription;
import org.fao.virtualrepository.spi.Writer;

public class TestRepository implements Repository {
	
	private final RepositoryDescription repository;
	
	private List<? extends Asset> assets;	
	
	public TestRepository() {
		this(new RepositoryDescription(new QName("test-repo")));
	}
	
	public TestRepository(RepositoryDescription description) {
		
		this(description, new CSVAsset("1", "asset-1",description), 
				   new CSVAsset("2", "asset-2",description), 
				   new CSVAsset("3", "asset-3",description));

	}
	
	public TestRepository(RepositoryDescription description, Asset ... assets) {
		this.assets = Arrays.asList(assets);
		this.repository = description;
		
	}
	
	@Override
	public RepositoryDescription description() {
		return repository;
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
