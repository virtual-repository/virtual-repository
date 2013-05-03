package org.fao.virtualrepository.sdmx;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.csv.CsvCodelist;
import org.fao.virtualrepository.impl.AbstractType;
import org.fao.virtualrepository.spi.RepositoryService;

/**
 * A {@link SdmxAsset} that represents codelists.
 * 
 * @author Fabio Simeoni
 *
 */
public class SdmxCodelist extends SdmxAsset {

	private static final String name = "sdmx/codelist";
	
	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final AssetType<SdmxCodelist> type = new AbstractType<SdmxCodelist>(name) {};

	/**
	 * Creates an instance with a given identifier, name, and {@link RepositoryService}.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param service the service
	 * @param properties the properties
	 */
	public SdmxCodelist(String id, String name, RepositoryService service) {
		super(type,id, name, service);
	}
}
