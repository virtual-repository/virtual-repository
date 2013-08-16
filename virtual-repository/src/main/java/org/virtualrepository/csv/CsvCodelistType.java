package org.virtualrepository.csv;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class CsvCodelistType extends AbstractType<CsvCodelist> {
	
	private static final String name = "csv/codelist";
	
	public CsvCodelistType() {
		super(name);
	}
}
