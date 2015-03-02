package org.virtualrepository.common;

import static org.virtualrepository.AssetType.*;

import javax.xml.namespace.QName;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.virtualrepository.AssetType;

@UtilityClass
public class Utils {
	
	
	
	
	public static void notNull(Object o) throws IllegalArgumentException {
		notNull("argument",o);
	}
	
	public static void notNull(AssetType type) throws IllegalArgumentException {
		if (type==null)
			throw new IllegalArgumentException("asset type is null");
	}
	
	public static void notNull(AssetType ... types) throws IllegalArgumentException {
		notNull("asset types",types);
		for (AssetType type : types)
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
	
	
	/**
	 * <code>true</code> if a given type is the same or a subtype of another.
	 */
	public static boolean ordered(@NonNull AssetType t1,  @NonNull AssetType t2) {
		
		return t1.equals(t2) || t2 == any;
	};
	
	/**
	 * <code>true</code> if a given api is the same or a subtype of another.
	 */
	public static boolean ordered(@NonNull Class<?> t1,  @NonNull Class<?> t2) {
		
		return t1.equals(t2) || t2.isAssignableFrom(t1);
	};

}
