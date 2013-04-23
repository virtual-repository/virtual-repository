package org.fao.virtualrepository.spi;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Properties;

public abstract class AbstractRepository implements Repository {

	private final QName name;
	private final Properties properties = new Properties();
	
	public AbstractRepository(QName name) {
		this.name=name;
	}
	
	@Override
	public QName name() {
		return name;
	}
	
	public Properties properties() {
		return properties;
	}
}
