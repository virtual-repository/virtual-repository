package org.acme;

import static java.util.Arrays.*;
import static org.acme.TestUtils.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.csv.CSV;
import org.fao.virtualrepository.csv.CSVStream2Table;
import org.fao.virtualrepository.csv.CSVTable;
import org.fao.virtualrepository.csv.Table2CSVStream;
import org.fao.virtualrepository.spi.RepositoryService;
import org.fao.virtualrepository.tabular.Column;
import org.fao.virtualrepository.tabular.DefaultTable;
import org.fao.virtualrepository.tabular.Row;
import org.fao.virtualrepository.tabular.Table;
import org.junit.Test;

public class CSVTableTest {

	static RepositoryService repo = mock(RepositoryService.class);
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidAsset() {
		
		CSV asset = anAsset(); //no columns, no header
		InputStream data = asStream(asset,someCSV());
		
		new CSVTable(asset,data);
		
	}
	
	@Test
	public void streamWithNoHeadersToTable() {
		
		String[][] data = someCSV(2,2);
		
		CSV asset  = anAssetWith("col1","col2");
		
		Table table = new CSVTable(asset,asStream(asset,data));
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void streamWithNoDefaultsToTable() {
		
		String[][] data = someCSV(2,2);
		
		CSV asset  = anAssetWith("col1","col2");
		
		asset.setDelimiter('\t');
		asset.setQuote('%');
		asset.setEncoding(Charset.forName("UTF-16"));
		
		Table table = new CSVTable(asset,asStream(asset,data));
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void streamWithHeadersToTable() {
		
		String[][] data ={{"col1","col2"},{"11","12"},{"21","22"}};
		
		CSV asset  = anAsset();
		
		asset.setHeader(true);
		
		Table table = new CSVTable(asset,asStream(asset,data));
		
		assertEquals(table,new String[][]{data[1],data[2]});
		
	}

	@Test
	public void roundTripCSVStream() throws Exception {
		
		String[][] data = someCSV(2,2);
		
		CSV asset  = anAssetWith("col1","col2");
		
		Table table = new CSVStream2Table().apply(asset,asStream(asset,data));
				
		InputStream stream = new Table2CSVStream().apply(asset,table);
		
		table = new CSVTable(asset, stream);
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void roundTripCSVTable() throws Exception {
		
		String[][] data = someCSV(2,2);
		
		CSV asset  = anAsset();
		
		Table table = asTable(data,"col1","col2");
				
		InputStream stream = new Table2CSVStream().apply(asset,table);
		
		table = new CSVStream2Table().apply(asset,stream);
		
		assertEquals(table,data);
		
	}
	
	//helpers
	
	Column[] columns(String ...names) {
		List<Column> list = new ArrayList<Column>();
		for (String name : names)
			list.add(new Column(name));
		return list.toArray(new Column[0]);
	}
	
	private CSV anAsset() {
		return new CSV("1","name",new TestRepo());
	}
	
	private CSV anAssetWith(String ... cols) {
		CSV asset = new CSV("1","name",repo);
		asset.setColumns(columns(cols));
		return asset;
	}
	
	private String[][] someCSV(int cols, int rows) {
		
		List<String[]> csv = new ArrayList<String[]>();
		for (int c =0; c<cols;c++) {
			List<String> row = new ArrayList<String>();
			for (int r=0;r<rows;r++)
				row.add(""+r+c);
			csv.add(row.toArray(new String[0]));
		}
		return csv.toArray(new String[0][]);
	}
	
	private Table asTable(String[][] data, String ... cols) {
		
		Column[] columns = columns(cols);
		List<Row> rows = new ArrayList<Row>();
		for (String[] row : data) {
			Map<QName,String> map = new HashMap<QName, String>(); 
			for (int i=0;i<row.length;i++)
					map.put(columns[i].name(),row[i]);
			rows.add(new Row(map));	
		}
		
		return new DefaultTable(asList(columns),rows);
	}
	
	private String[][] someCSV() {
		return someCSV(2,2);
	}
} 
