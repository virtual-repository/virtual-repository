package org.fao.virtualrepository.tabular;

import java.util.List;



/**
 * A table of {@link Column}s and {@link Row}s.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Table extends Iterable<Row> {

	/**
	 * Returns the columns of this table.
	 * 
	 * @return the columns
	 */
	List<Column> columns();
}
