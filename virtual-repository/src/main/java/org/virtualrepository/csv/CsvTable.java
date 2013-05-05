package org.virtualrepository.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import au.com.bytecode.opencsv.CSVReader;

/**
 * A {@link Table} backed up by an {@link InputStream} of CSV data.
 * 
 * @author Fabio Simeoni
 * 
 */
public class CsvTable implements Table {

	private final Table inner;

	/**
	 * Creates an instance for a given {@link CsvAsset} asset and {@link InputStream}.
	 * 
	 * @param asset the asset
	 * @param stream the stream
	 * 
	 * @throws IllegalArgumentException if the asset is inconsistently described
	 */
	public CsvTable(CsvAsset asset, InputStream stream) {
		
		CSVReader reader = validateAssetAndBuildReader(asset, stream);
		
		RowIterator iterator = new RowIterator(asset, reader);

		inner = new DefaultTable(asset.columns(), iterator);
	}
	
	// helper
	private CSVReader validateAssetAndBuildReader(CsvAsset asset,InputStream stream) {
		
		CSVReader reader = new CSVReader(new InputStreamReader(stream, asset.encoding()),asset.delimiter(),asset.quote());

		List<Column> columns  =new ArrayList<Column>();
		
		if (asset.hasHeader())
			try {
				for (String name : reader.readNext())
					columns.add(new Column(name));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("invalid CSV asset " + asset.id() + ": cannot read stream",e);
			}
		
		if (!asset.properties().contains(CsvAsset.columns)) // no columns
			if (columns.isEmpty()) //no header either
				throw new IllegalArgumentException("invalid CSV asset description " + asset.id() + ": columns are missing and there is no indication of a header where to find them");
			else
				asset.setColumns(columns.toArray(new Column[0]));
		
		return reader;
	}

	@Override
	public Iterator<Row> iterator() {
		return inner.iterator();
	}

	@Override
	public List<Column> columns() {
		return inner.columns();
	}

	// iterates over rows pulling them from the reader
	static class RowIterator implements Iterator<Row> {

		private static final Logger log = LoggerFactory.getLogger(CsvTable.class);

		private final Map<QName, String> data = new HashMap<QName, String>();

		private CsvAsset asset;
		private final CSVReader reader;

		private String[] row;
		private Throwable error;
		private int count;

		public RowIterator(CsvAsset asset, CSVReader reader) {

			this.reader = reader;
			this.asset=asset;

		}

		public boolean hasNext() {

			if (row!=null)
				return true;
			
			if (asset.rows() <= count) {
				close();
				return false;
			}

			try {
				row = reader.readNext();
				count++;
			} catch (IOException e) {
				error = e;
			}

			return row != null;
		}

		public Row next() {
			
			try {
				checkRow();
			}
			catch(RuntimeException e) {
				close();
			}
			
			Row result = buildRow();
			
			row=null;
			
			return result;
		}

		// helper
		private void checkRow() {

			if (error != null)
				throw new RuntimeException(error);

			if (row == null && !this.hasNext()) // reads ahead
				throw new NoSuchElementException();

			if (row.length > asset.columns().size())
				throw new RuntimeException("invalid CSV data: row " + row + " has more columns than expected ("
						+ asset.columns() + ")");

		}

		// helper
		private Row buildRow() {

			data.clear();

			for (int i = 0; i < row.length; i++)
				data.put(asset.columns().get(i).name(), row[i]);

			return new Row(data);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void close() {
			try {
				reader.close();
			} catch (Exception e) {
				log.warn("could not close CSV stream", e);
			}
		}
	}

}