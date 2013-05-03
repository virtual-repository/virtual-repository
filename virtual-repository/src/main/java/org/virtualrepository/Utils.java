package org.virtualrepository;

import javax.xml.namespace.QName;

import org.virtualrepository.spi.RepositoryService;

/**
 * Library-wide constants and utilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {
	
	public static void notNull(Object o) throws IllegalArgumentException {
		notNull("argument",o);
	}
	
	public static void notNull(AssetType<?> type) throws IllegalArgumentException {
		if (type==null)
			throw new IllegalArgumentException("asset type is null");
	}
	
	public static void notNull(AssetType<?> ... types) throws IllegalArgumentException {
		notNull("asset types",types);
		for (AssetType<?> type : types)
			notNull(type);
	}
	
	public static void notNull(String name, Object o) throws IllegalArgumentException {
		if (o==null)
			throw new IllegalArgumentException(name+" is null");
	}
	
	public static void notEmpty(String name, String o) throws IllegalArgumentException {
		if (o.isEmpty())
			throw new IllegalArgumentException(name+" is empty");
	}
	
	public static void valid(String name, String o) throws IllegalArgumentException {
		notNull(name, o);
		notEmpty(name,o);
	}
	
	public static void valid(QName name) throws IllegalArgumentException {
		notNull("name", name);
		valid("name",name.getLocalPart());
	}
	
	public static void valid(String text,QName name) throws IllegalArgumentException {
		notNull(text, name);
		valid(text,name.getLocalPart());
	}
	
	public static void valid(RepositoryService service) throws IllegalArgumentException {
		try {
			notNull("repository service",service);
			valid(service.name());
			notNull("repository browser",service.browser());
			notNull("repository importers",service.importers());
			notNull("repository publishers",service.publishers());

		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid repository service",e);
		}
	}

}
