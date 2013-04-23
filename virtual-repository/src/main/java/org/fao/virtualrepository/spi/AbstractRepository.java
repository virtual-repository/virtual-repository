package org.fao.virtualrepository.spi;

import static org.fao.virtualrepository.Utils.*;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.Properties;

/**
 * Partial Repository implementation.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class AbstractRepository implements Repository {

	private final QName name;
	private final Properties properties = new Properties();

	/**
	 * Creates an instance with a given name.
	 * 
	 * @param name the name
	 */
	protected AbstractRepository(QName name) {
		
		notNull(name);
		
		this.name = name;
	}

	@Override
	public QName name() {
		return name;
	}

	@Override
	public Properties properties() {
		return properties;
	}
}
