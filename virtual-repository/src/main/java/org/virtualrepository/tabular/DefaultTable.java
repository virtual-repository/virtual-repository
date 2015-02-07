package org.virtualrepository.tabular;

import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

/**
 * Base {@link Table} implementation.
 * 
 * @author Fabio Simeoni
 *
 */
public class DefaultTable extends AbstractTable implements Table {

	private final Iterator<Row> rows;

	/**
	 * Creates an instance with given columns and rows.
	 * @param columns the columns
	 * @param rows the rows
	 */
	public DefaultTable(List<Column> columns, @NonNull Iterable<Row> rows) {
		
		//if we delegate to other constructor here, we couldnt check for null
		
		super(columns);
		
		this.rows= rows.iterator();
	}
	
	/**
	 * Creates an instance with given columns and rows.
	 * @param columns the columns
	 * @param rows the rows
	 */
	public DefaultTable(List<Column> columns, @NonNull Iterator<Row> rows) {
		
		super(columns);
		
		this.rows= rows;
	}
	
	@Override
	public Iterator<Row> iterator() {
		return rows;
	}
	
	
	

}
