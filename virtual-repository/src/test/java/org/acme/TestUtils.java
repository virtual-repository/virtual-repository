package org.acme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashSet;

import org.fao.virtualrepository.csv.CsvAsset;
import org.fao.virtualrepository.tabular.Column;
import org.fao.virtualrepository.tabular.Row;
import org.fao.virtualrepository.tabular.Table;
import org.junit.Assert;

import au.com.bytecode.opencsv.CSVWriter;

public class TestUtils {

	
	public static void assertEqualElements(Iterable<?> it1, Iterable<?> it2) {
		Assert.assertEquals(toCollection(it1),toCollection(it2));
	}
	
	public static <T> Collection<T> toCollection(Iterable<T> it) {
		HashSet<T> set = new HashSet<T>();
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
