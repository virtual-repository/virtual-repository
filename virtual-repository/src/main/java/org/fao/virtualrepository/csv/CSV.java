package org.fao.virtualrepository.csv;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.Property;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.RepositoryService;

/**
 * A CSV data asset.
 * 
 * @author Fabio Simeoni
 *
 */
public class CSV extends AbstractAsset {

	public static  final String delimiter= "delimiter";
	public static  final char defaultDelimiter= ',';
	
	public static Property<Character> delimiter(char d) {
		return new Property<Character>(delimiter,d,"column delimiter character");
	}
	/**
	 * The type of CSV assets.
	 */
	public static AssetType<CSV> type = new AssetType<CSV>() {
		
		public static final String NAME = "text/csv";
		
		public QName name() {
			return new QName(NAME);
		};
		
		public String toString() {
			return NAME;
		};
	};
	
	/**
	 * Creates an instance with a given identifier, name, origin and zero or more properties.
	 * @param id the identifier
	 * @param name the name
	 * @param origin the origin
	 * @param properties the properties
	 */
	public CSV(String id, String name, RepositoryService origin, Property<?> ... properties) {
		super(id,name,origin);
		
		if (!properties().contains(delimiter))
			properties().add(delimiter(defaultDelimiter));
	}
	
	@Override
	public AssetType<CSV> type() {
		return type;
	}

	
}
