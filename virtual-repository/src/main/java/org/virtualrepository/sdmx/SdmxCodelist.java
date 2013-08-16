package org.virtualrepository.sdmx;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.impl.Type;

/**
 * A {@link SdmxAsset} that represents codelists.
 * 
 * @author Fabio Simeoni
 * 
 */
@XmlRootElement(name = "sdmx-codelist")
public class SdmxCodelist extends SdmxAsset {

	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final Type<SdmxCodelist> type = new SdmxCodelistType();

	// not part of public API, for JAXB de-serialisation only
	SdmxCodelist() {
	}

	/**
	 * Creates an instance with a given URN, identifier, version, and a name.
	 * <p>
	 * Use as a plugin-facing constructor for codelist discovery and retrieval purposes.
	 * 
	 * @param urn the URN
	 * @param id the identifier
	 * @param version the version
	 * @param name the name
	 * @param service the service
	 * @param properties the properties
	 */
	public SdmxCodelist(String urn, String id, String version, String name) {
		super(type, urn, id, version, name);
	}

	/**
	 * Creates an instance with a given target service.
	 * <p>
	 * Use as a client-facing constructor for codelist publication.
	 * 
	 * @param name the name
	 * @param service the service
	 */
	public SdmxCodelist(String name,RepositoryService service) {
		super(type, name,service);
	}
	
}
