package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.impl.AbstractType;
import org.fao.virtualrepository.spi.RepositoryService;

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
	public static final AssetType<CsvCodelist> type = new AbstractType<CsvCodelist>(name) {};

	/**
	 * Creates an instance with a given identifier, name, and repository.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	public CsvCodelist(String id, String name, RepositoryService repository) {
		super(type,id, name, repository);
	}
	
}
