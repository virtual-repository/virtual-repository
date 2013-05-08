package org.acme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.virtualrepository.csv.CsvAsset;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import au.com.bytecode.opencsv.CSVWriter;

public class TestUtils {

	
	public static void assertEqualElements(Iterable<?> it1, Iterable<?> it2) {
		Assert.assertEquals(asList(it1),asList(it2));
	}
	
	public static <T> List<T> asList(Iterable<T> it) {
		List<T> set = new ArrayList<T>();
		for (T t : it)
			set.add(t);
		return set;
	}
	
	public static InputStream asStream(CsvAsset asset,String[][] data) {
		
		try {
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, asset.encoding()), asset.delimiter(), asset.quote());
			for (String[] row : data)
				writer.writeNext(row);
			writer.flush();
			writer.close();
			return new ByteArrayInputStream(out.toByteArray());
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void assertEquals(Table table,String[][] data) {
			
			int i = 0;
			for (Row row : table) {
				int j=0;
				System.out.println(row);
				for (Column column : table.columns()) {
					if (!row.get(column).equals(data[i][j]))
							Assert.fail();
					else
						j++;
				}
				i++;
			}
			
			Assert.assertEquals(i,data.length);
		}

}
