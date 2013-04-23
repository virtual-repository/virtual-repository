package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.Property;
import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.spi.AbstractRepository;
import org.fao.virtualrepository.spi.Reader;
import org.fao.virtualrepository.spi.Writer;

public class TestRepository extends AbstractRepository {
	
	private TestReader reader = new TestReader("test-content");
	
	private final List<CSV> assets = new ArrayList<CSV>();	
	
	public TestRepository() {
		this("test-repo");
	}
	
	public TestRepository(String name) {
		super(new QName(name));
		
	}
	
	public void addAssets(CSV ... assets) {
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
	
	public static Property<String> testprop(String value) {
			return new Property<String>(testprop, value, " a test propery");
	}
	public static final String testprop = "test-prop";

	public class TestReader implements Reader<CSV,String> {
		
		private final String content;
		
		public TestReader(String content) {
			this.content=content;
		}
		
		@Override
		public Iterable<CSV> find() {
			return assets;
		}
		
		@Override
		public AssetType<CSV> type() {
			return CSV.type;
		}
		
		@Override
		public Class<String> api() {
			return String.class;
		}
		
		@Override
		public String fetch(CSV asset) {
			return content;
		}
	}
}
