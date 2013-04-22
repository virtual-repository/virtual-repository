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
	
	private TestReader reader = new TestReader("test-content");
	
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
	
	public void setReader(TestReader reader) {
		this.reader=reader;
	}
	
	@Override
	public List<? extends Reader<?>> readers() {
		return singletonList(reader);
	}
	
	@Override
	public List<? extends Writer<?>> writers() {
		return emptyList();
	}
	
	@Override
	public String toString() {
		return assets.toString();
	}

	public class TestReader implements Reader<String> {
		
		private final String content;
		
		public TestReader(String content) {
			this.content=content;
		}
		
		@Override
		public Iterable<? extends Asset> find() {
			return assets;
		}
		
		@Override
		public AssetType<?> type() {
			return new CSV();
		}
		
		@Override
		public Class<String> api() {
			return String.class;
		}
		
		@Override
		public String fetch(Asset asset) {
			return content;
		}
	}
}
