package org.virtualrepository.tabular;

import java.util.List;

import org.virtualrepository.Properties;
import org.virtualrepository.impl.Described;



/**
 * A table of {@link Column}s and {@link Row}s, with optional {@link Properties}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Table extends Iterable<Row>, Described {

	/**
	 * Returns the columns of this table.
	 * 
	 * @return the columns
	 */
	List<Column> columns();
}
