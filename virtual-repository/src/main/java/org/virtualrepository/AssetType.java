package org.virtualrepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



public interface AssetType {
	

	public static final AssetType any = AssetType.of("___any___");

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
	static class Simple implements AssetType {
		
		@NonNull @Getter
		private final String name;
	
		@Override
		public String toString() {
			return name;
		}
	}
}
