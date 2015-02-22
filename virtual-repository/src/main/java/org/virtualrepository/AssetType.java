package org.virtualrepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



public interface AssetType {

	/**
	 * The name of this type.
	 */
	String name();
	
	
	//type param helps to correlate signatures at the point of use
	@RequiredArgsConstructor(staticName="typeof")
	@EqualsAndHashCode
	static class Private<A extends Asset> implements AssetType {
		
		@NonNull @Getter
		private final String name;
		
	}
}
