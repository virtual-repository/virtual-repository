package org.fao.virtualrepository.csv;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.AssetType;
import org.fao.virtualrepository.impl.AbstractAsset;
import org.fao.virtualrepository.spi.Repository;

public class CSV extends AbstractAsset<CSV> {

	/**
	 * The type of CSV assets.
	 */
	public static AssetType<CSV> type = new AssetType<CSV>() {
		
		public static final String NAME = "text/csv";
		
		public QName name() {
			return new QName(NAME);
		};
	};
	
	public CSV(String id, String name, Repository origin) {
		super(id,name,origin);
	}
	
	@Override
	public AssetType<CSV> type() {
		return type;
	}
	
	@Override
	public String toString() {
		return "CSVAsset [id=" + id() + ", name=" + name() + "]";
	}

	
}
