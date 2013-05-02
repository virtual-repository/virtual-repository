package org.fao.virtualrepository.sdmx;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.Property;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.RepositoryService;

public class SDMX extends AbstractAsset {

	public static final String ns= "http://www.SDMX.org/resources/SDMXML/schemas/v2_0/structure";
	public static final QName name = new QName(ns, "CodeList");
	
	/**
	 * The type of SDMX assets.
	 */
	public static AssetType<SDMX> type = new AssetType<SDMX>() {

		public QName name() {
			return name;
		};

		public String toString() {
			return name.toString();
		};
	};

	/**
	 * Creates an instance with a given identifier, name, and repository.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param repository the repository
	 * @param properties the properties
	 */
	public SDMX(String id, String name, RepositoryService origin, Property<?> ... properties) {
		super(id, name, origin, properties);
	}

	@Override
	public AssetType<?> type() {
		return type;
	}

}
