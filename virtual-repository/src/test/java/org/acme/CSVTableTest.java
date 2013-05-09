package org.acme;

import static java.util.Arrays.*;
import static org.acme.TestUtils.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.virtualrepository.csv.CsvAsset;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.CsvStream2Table;
import org.virtualrepository.csv.CsvTable;
import org.virtualrepository.csv.Table2CsvStream;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

public class CSVTableTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidAsset() {
		
		CsvAsset asset = anAsset(); //no columns, no header
		InputStream data = asStream(asset,someCSV());
		
		new CsvTable(asset,data);
		
	}
	
	@Test
	public void streamWithNoHeadersToTable() {
		
		String[][] data = someCSV(2,2);
		
		CsvAsset asset  = anAssetWith("col1","col2");
		
		
		Table table = new CsvTable(asset,asStream(asset,data));
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void streamWithNoDefaultsToTable() {
		
		String[][] data = someCSV(2,2);
		
		CsvAsset asset  = anAssetWith("col1","col2");
		
		asset.setDelimiter('\t');
		asset.setQuote('%');
		asset.setEncoding(Charset.forName("UTF-16"));
		
		Table table = new CsvTable(asset,asStream(asset,data));
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void streamWithHeadersToTable() {
		
		String[][] data ={{"col1","col2"},{"11","12"},{"21","22"}};
		
		CsvAsset asset  = anAsset();
		
		asset.setHeader(true);
		
		Table table = new CsvTable(asset,asStream(asset,data));
		
		assertEquals(table,new String[][]{data[1],data[2]});
		
	}

	@Test
	public void roundTripCSVStream() throws Exception {
		
		String[][] data = someCSV(2,2);
		
		CsvAsset asset  = anAssetWith("col1","col2");
		
		Table table = new CsvStream2Table().apply(asset,asStream(asset,data));
				
		InputStream stream = new Table2CsvStream().apply(asset,table);
		
		table = new CsvTable(asset, stream);
		
		assertEquals(table,data);
		
	}
	
	@Test
	public void roundTripCSVTable() throws Exception {
		
		String[][] data = someCSV(2,2);
		
		CsvAsset asset  = anAsset();
		
		Table table = asTable(data,"col1","col2");
				
		InputStream stream = new Table2CsvStream().apply(asset,table);
		
		table = new CsvStream2Table().apply(asset,stream);
		
		assertEquals(table,data);
		
	}
	
	//helpers
	
	Column[] columns(String ...names) {
		List<Column> list = new ArrayList<Column>();
		for (String name : names)
			list.add(new Column(name));
		return list.toArray(new Column[0]);
	}
	
	private CsvAsset anAsset() {
		return new CsvCodelist("1","name",1);
	}
	
	private CsvAsset anAssetWith(String ... cols) {
		CsvAsset asset = anAsset();
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
