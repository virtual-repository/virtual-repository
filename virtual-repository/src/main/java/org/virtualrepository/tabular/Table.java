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
	
	/**
	 * Returns a table which can be iterated over multiple times.
	 * <p>
	 * It may return this very table if it is already materialised.
	 * @return the materialised table.
	 */
	default Table materialise() {
		
		List<Row> rows = new ArrayList<Row>();
		
		for (Row row : rows) 
			rows.add(row);
		
		return new MaterializedTable(columns(), rows);
	}
	
	/**
	 * Returns a sequential stream of the rows of this table.
	 * @return the row stream
	 */
	default Stream<Row> stream() {
		
		return stream(false);
	}
	
	/**
	 * Returns a stream of the rows of this table.
	 * @param parallel <code>true</code> if the stream is to be consumed in parallel, <code>false</code> otherwise.
	 * @return the row stream
	 *
	 */
	default Stream<Row> stream(boolean parallel) {
		
		Iterable<Row> it = () -> iterator();
		return StreamSupport.<Row>stream(it.spliterator(), parallel);
	}
}
