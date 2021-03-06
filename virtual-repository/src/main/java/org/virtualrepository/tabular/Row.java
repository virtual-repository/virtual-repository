package org.virtualrepository.tabular;

import static org.virtualrepository.Utils.*;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 * A row of a {@link Table}.
 * 
 * @author Fabio Simeoni
 *
 */
public class Row {

	Map<QName,String> row;
	
	/**
	 * Creates an instance with the named values of the row.
	 * @param data
	 */
	public Row(Map<QName,String> data) {
		
		notNull(data);
		
		this.row=data;
	}
	
	/**
	 * Returns the value of this row for a given column.
	 * @param column the column's name
	 * @return the value
	 */
	public String get(QName name) {
		return row.get(name);
	}
	
	/**
	 * Returns the value of this row for a given column.
	 * @param column the column's name
	 * @return the value
	 */
	public String get(String name) {
		return row.get(new QName(name));
	}
	
	/**
	 * Returns the value of this row for a given column.
	 * @param column the column
	 * @return the value
	 */
	public String get(Column column) {
		return this.get(column.name());
	}
	
	@Override
	public String toString() {
		return row.toString();
	}
}
