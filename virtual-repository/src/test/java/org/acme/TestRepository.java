package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.Writer;

public class TestRepository implements Repository {
	
	private final QName name;
	
	private final List<Asset> assets = new ArrayList<Asset>();	
	
	public TestRepository() {
		this("test-repo");
	}
	
	public TestRepository(String name) {
		this.name = new QName(name);
		
	}
	
	@Override
	public QName name() {
		return name;
	}
	
	public void addAssets(Asset ... assets) {
		this.assets.addAll(asList(assets));
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
