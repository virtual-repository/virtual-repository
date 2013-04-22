package org.fao.virtualrepository.impl;

import org.fao.virtualrepository.Format;

public abstract class AbstractFormat implements Format {

	private final String name;
	
	protected AbstractFormat(String name) {
		this.name=name;
	}
	
	public String name() {
		return name;
	}
}
