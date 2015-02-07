package org.virtualrepository.tabular;

import java.util.Iterator;
import java.util.List;

import lombok.NonNull;

/**
 * A {@link Table} that materialises its elements and can be iterated over multiple times.
 * 
 */
public class MaterializedTable extends AbstractTable implements Table {

	private final Iterable<Row> rows;

	/**
	 * Creates an instance with given columns and rows.
	 * @param columns the columns
	 * @param rows the rows
	 */
	public MaterializedTable(List<Column> columns, @NonNull Iterable<Row> rows) {
		
		super(columns);
		
		this.rows= rows;
	}
	
	
	@Override
	public Iterator<Row> iterator() {
		return rows.iterator();
	}
	
	
	

}
