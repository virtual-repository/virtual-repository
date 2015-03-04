package org.virtualrepository.common;

import static org.virtualrepository.AssetType.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.virtualrepository.AssetType;

@UtilityClass
public class Utils {
	
	
	public void notNull(String name, Object o) throws IllegalArgumentException {
		if (o==null)
			throw new IllegalArgumentException(name+" is null");
	}
	
	
	/**
	 * <code>true</code> if a given type is the same or a subtype of another.
	 */
	public boolean ordered(@NonNull AssetType t1,  @NonNull AssetType t2) {
		
		return t1.equals(t2) || t2 == any;
	};
	
	/**
	 * <code>true</code> if a given api is the same or a subtype of another.
	 */
	public boolean ordered(@NonNull Class<?> t1,  @NonNull Class<?> t2) {
		
		return t1.equals(t2) || t2.isAssignableFrom(t1);
	};
}
