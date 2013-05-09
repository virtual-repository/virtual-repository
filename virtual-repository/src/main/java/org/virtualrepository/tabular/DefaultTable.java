package org.virtualrepository.tabular;

import static org.virtualrepository.Utils.*;

import java.util.Iterator;
import java.util.List;

import org.virtualrepository.Properties;

/**
 * Base {@link Table} implementation.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultTable implements Table {

	private final List<Column> columns;
	private final Iterator<Row> rows;
	private final Properties properties = new Properties();
	
	/**
	 * Creates an instance with given columns and rows
	 * @param columns the columns
	 * @param rows the rows
	 */
	public DefaultTable(List<Column> columns, Iterable<Row> rows) {
		this(columns,rows.iterator());
	}
	
	/**
	 * Creates an instance with given columns and rows
	 * @param columns the columns
	 * @param rows the rows
	 */
	public DefaultTable(List<Column> columns, Iterator<Row> rows) {
		
		notNull("columns",columns);
		notNull("rows",rows);
		
		this.columns=columns;
		this.rows=rows;
	}
	
	@Override
	public Iterator<Row> iterator() {
		return rows;
	}

	@Override
	public List<Column> columns() {
		return columns;
	}
	
	@Override
	public Properties properties() {
		return properties;
	}
	
	
	

}
