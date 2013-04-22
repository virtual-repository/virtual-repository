package org.fao.virtualrepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Module-wide utilities
 * @author Fabio Simeoni
 *
 */
public class Utils {
	
	public static void notNull(Object o) throws IllegalArgumentException {
		notNull("argument",o);
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
	
	public static void valid(File file) throws IllegalArgumentException {
		
		notNull("file", file);
		
		if (!file.exists() || file.isDirectory() || !file.canRead())
			throw new IllegalArgumentException(file+" does not exist, is a directory, or cannot be read");
	}
	
	public static void valid(QName name) throws IllegalArgumentException {
		notNull("name", name);
		valid("name",name.getLocalPart());
	}
	
	public static void valid(String text,QName name) throws IllegalArgumentException {
		notNull(text, name);
		valid(text,name.getLocalPart());
	}
	
	public static QName copyName(QName name) {
		return new QName(name.getNamespaceURI(),name.getLocalPart());
	}
	
	
	public static <T> T reveal(Object publicObject, Class<T> privateClass) {
		
		notNull(publicObject);
		
		try {
			return privateClass.cast(publicObject);
		}
		catch(ClassCastException e) {
			throw new IllegalArgumentException("expected a "+privateClass+ "found instead a "+publicObject.getClass());
		}
	}
	
	public static <PUBLIC, PRIVATE extends PUBLIC > List<PRIVATE> reveal(Iterable<? extends PUBLIC> publicObjects, Class<PRIVATE> privateClass) {
		
		notNull(publicObjects);
		
		List<PRIVATE> privates = new ArrayList<PRIVATE>();
		
		for (PUBLIC publicObject : publicObjects)
			privates.add(reveal(publicObject,privateClass));
		
		return privates;
	}

}
