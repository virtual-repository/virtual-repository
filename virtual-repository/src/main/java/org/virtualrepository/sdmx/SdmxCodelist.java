package org.virtualrepository.sdmx;

import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.RepositoryService;

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
	public static final Type<SdmxCodelist> type = new AbstractType<SdmxCodelist>(name) {};

	/**
	 * Creates an instance with a given URN, identifier, version, name, and {@link RepositoryService}.
	 * 
	 * @param urn the URN
	 * @param id the identifier
	 * @param id the version
	 * @param name the name
	 * @param service the service
	 * @param properties the properties
	 */
	public SdmxCodelist(String urn, String id, String version, String name, RepositoryService service) {
		super(type,urn,id,version, name, service);
	}
	
	/**
	 * Creates an instance with a given {@link RepositoryService}, suitable for asset publication.
	 * 
	 * @param service the service
	 */
	public SdmxCodelist(RepositoryService service) {
		super(type,service);
	}
}
