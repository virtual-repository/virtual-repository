package org.virtualrepository.csv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.virtualrepository.spi.Transform;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * A {@link Transform} from {@link InputStream} to {@link Table} for {@link CsvAsset} assets.
 * 
 * @author Fabio Simeoni
 *
 */
public class Table2CsvStream<T extends CsvAsset> implements Transform<T,Table,InputStream> {
	
	@Override
	public InputStream apply(CsvAsset asset,Table table) throws Exception {
		
		//we do it in memory for now
		StringWriter stream = new StringWriter();
		
		CSVWriter writer = new CSVWriter(stream, asset.delimiter(),asset.quote());
		
		List<String> values = new ArrayList<String>();
		
		//table may not originate from stream but directly from table
		//even if it does, we take the table as authoritative
		asset.setColumns(table.columns().toArray(new Column[0]));
		
		for (Row row : table) {
			values.clear();
			for (Column column : table.columns())
				values.add(row.get(column));
			writer.writeNext(values.toArray(new String[0]));
		}
		
		writer.flush();
		writer.close();
		
		return new ByteArrayInputStream(stream.toString().getBytes(asset.encoding()));
	}
	
	@Override
	public Class<Table> inputAPI() {
		return Table.class;
	}
	
	@Override
	public Class<InputStream> outputAPI() {
		return InputStream.class;
	}
}
