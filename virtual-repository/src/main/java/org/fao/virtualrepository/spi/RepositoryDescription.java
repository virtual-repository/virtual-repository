package org.fao.virtualrepository.spi;

import javax.xml.namespace.QName;

public class RepositoryDescription {

	private final QName name;
	
	public RepositoryDescription(QName name) {
		this.name=name;
	}
	
	public QName name() {
		return name;
	}
	
}
