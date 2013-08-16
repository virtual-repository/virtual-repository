package org.virtualrepository.csv;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class CsvGenericType extends AbstractType<CsvAsset> {
	
	private static final String name = "csv/generic";
	
	public CsvGenericType() {
		super(name);
	}
}
