package org.fao.virtualrepository.csv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.fao.virtualrepository.spi.Transform;
import org.fao.virtualrepository.tabular.Column;
import org.fao.virtualrepository.tabular.Row;
import org.fao.virtualrepository.tabular.Table;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * A {@link Transform} from {@link InputStream} to {@link Table} for {@link CSV} assets.
 * 
 * @author Fabio Simeoni
 *
 */
public class Table2CSVStream implements Transform<CSV,Table,InputStream> {
	
	@Override
	public InputStream apply(CSV asset,Table table) throws Exception {
		
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
