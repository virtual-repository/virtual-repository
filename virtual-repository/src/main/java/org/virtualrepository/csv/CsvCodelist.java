package org.virtualrepository.csv;

import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;

/**
 * A {@link CsvAsset} that represents a codelist.
 * 
 * @author Fabio Simeoni
 *
 */
public class CsvCodelist extends CsvAsset {
	
	private static final String name = "csv/codelist";
	
	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final Type<CsvCodelist> type = new AbstractType<CsvCodelist>(name) {};

	/**
	 * Creates an instance with a given identifier, and name.
	 * 
	 * @param name the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	public CsvCodelist(String id, String name) {
		super(type,id, name);
	}
	
}
