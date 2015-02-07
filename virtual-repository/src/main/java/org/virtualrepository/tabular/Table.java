package org.virtualrepository.tabular;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.virtualrepository.Properties;
import org.virtualrepository.impl.Described;



/**
 * A table of {@link Column}s and {@link Row}s, with optional {@link Properties}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Table extends Iterable<Row>,  Described {

	/**
	 * Returns the columns of this table.
	 * 
	 * @return the columns
	 */
	List<Column> columns();
	
	
	default Table materialise() {
		
		List<Row> rows = new ArrayList<Row>();
		
		for (Row row : rows) 
			rows.add(row);
		
		return new MaterializedTable(columns(), rows);
	}
	
	default Stream<Row> stream() {
		
		return stream(false);
	}
	
	default Stream<Row> stream(boolean parallel) {
		
		Iterable<Row> it = () -> iterator();
		return StreamSupport.<Row>stream(it.spliterator(), parallel);
	}
}
