package org.virtualrepository.common;

import static org.virtualrepository.Types.*;
import static org.virtualrepository.common.Utils.Comparison.*;

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
	 * Four value comparison logic.
	 *
	 */
	public static enum Comparison {
		
		SUBTYPE, SUPERTYPE, EQUALS, UNRELATED 
	}
	
	/**
	 * Compares this asset type to another based on a four value logic.
	 * @see Comparison
	 */
	public static Comparison compare(@NonNull AssetType t1,  @NonNull AssetType t2) {
		
		return t1.equals(t2) ? EQUALS : t1 == any ? SUPERTYPE : t2 == any? SUBTYPE : UNRELATED;
	};
	
	
//	public static Comparison compare(Accessor<?> a1, Accessor<?> a2) {
//
//		Comparison comparison = compare(a1.type(),a2.type());
//		
//		return comparison != EQUALS ?
//							    comparison :
//								a1.api()==a2.api() ? EQUALS :
//									a1.api().isAssignableFrom(a2.api()) ?  SUPERTYPE :
//									a2.api().isAssignableFrom(a1.api()) ?  SUBTYPE : 
//									
//								    UNRELATED;
//	}

}
