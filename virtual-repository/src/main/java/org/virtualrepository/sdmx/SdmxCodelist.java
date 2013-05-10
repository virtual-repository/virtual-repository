package org.virtualrepository.sdmx;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;

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
	 * Creates an instance with a given URN, identifier, version, and a name.
	 * 
	 * @param urn the URN
	 * @param name the identifier
	 * @param name the version
	 * @param name the name
	 * @param service the service
	 * @param properties the properties
	 */
	public SdmxCodelist(String urn, String id, String version, String name) {
		super(type,urn,id,version, name);
	}
	
	/**
	 * Creates an instance with a given {@link RepositoryService}, suitable for asset publication.
	 * 
	 * @param name the name of the asset
	 * @param service the service
	 */
	public SdmxCodelist(String name,RepositoryService service) {
		super(type,name,service);
	}
}
