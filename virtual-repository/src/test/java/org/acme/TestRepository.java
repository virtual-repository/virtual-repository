package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.fao.virtualrepository.AssetType.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.csv.CSVAsset;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Repository;
import org.fao.virtualrepository.spi.Writer;

public class TestRepository implements Repository {
	
	
	private final QName name;
	
	private TestReader reader = new TestReader("test-content");
	
	private final List<CSVAsset> assets = new ArrayList<CSVAsset>();	
	
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
	
	public void addAssets(CSVAsset ... assets) {
		this.assets.addAll(asList(assets));
	}
	
	public void setReader(TestReader reader) {
		this.reader=reader;
	}
	
	@Override
	public List<? extends Reader<?,?>> readers() {
		return singletonList(reader);
	}
	
	@Override
	public List<? extends Writer<?,?>> writers() {
		return emptyList();
	}
	
	@Override
	public String toString() {
		return assets.toString();
	}

	public class TestReader implements Reader<CSVAsset,String> {
		
		private final String content;
		
		public TestReader(String content) {
			this.content=content;
		}
		
		@Override
		public Iterable<CSVAsset> find() {
			return assets;
		}
		
		@Override
		public AssetType<CSVAsset> type() {
			return CSV;
		}
		
		@Override
		public Class<String> api() {
			return String.class;
		}
		
		@Override
		public String fetch(CSVAsset asset) {
			return content;
		}
	}
}
