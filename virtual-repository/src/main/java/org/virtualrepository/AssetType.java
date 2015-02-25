package org.virtualrepository;

import static org.virtualrepository.Types.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;



public interface AssetType extends Comparable<AssetType> {

	/**
	 * The name of this type.
	 */
	String name();
	
	
	
	default public int compareTo(@NonNull AssetType other) {
		
		return this.equals(other) ? 0 : this == any ? 1 : other == any? -1 
				
				: 0; //Incomparable
	};
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	static AssetType of(@NonNull String name) {
		
		return new Simple(name);
	}

	//type param helps to correlate signatures at the point of use
	@RequiredArgsConstructor
	@EqualsAndHashCode
	@ToString
	static class Simple implements AssetType {
		
		@NonNull @Getter
		private final String name;
		
	}
}
