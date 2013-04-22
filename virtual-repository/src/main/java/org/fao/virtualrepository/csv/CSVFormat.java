package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.impl.AbstractFormat;

public final class CSVFormat extends AbstractFormat {
	
	static CSVFormat format = new CSVFormat();
	
	public static final String NAME = "CSV";
	
	private CSVFormat() {
		super(NAME);
	}
}
