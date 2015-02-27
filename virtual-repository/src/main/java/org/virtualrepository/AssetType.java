package org.virtualrepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;



public interface AssetType {

	/**
	 * The name of this type.
	 */
	String name();
	
	
	
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
