package org.virtualrepository;

import static java.util.Collections.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



public interface AssetType {
	

	public static final AssetType any = AssetType.of("any");

	/**
	 * The name of this type.
	 */
	String name();
	
	/**
	 * The types specialised by this type.
	 */
	default Collection<AssetType> specialises() {
		return emptyList();
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	static Simple of(@NonNull String name) {
		
		return new Simple(name);
	}

	//type param helps to correlate signatures at the point of use
	@RequiredArgsConstructor
	@EqualsAndHashCode
	static class Simple implements AssetType {
		
		@NonNull @Getter
		private final String name;
		
		private final Set<AssetType> supertypes = new HashSet<>();
	
		@Override
		public String toString() {
			return name;
		}
		
		public Simple specialises(AssetType ... types) {
			return specialises(Arrays.asList(types));
		}
		
		public Simple specialises(Iterable<AssetType> types) {
			
			types.forEach(supertypes::add);
			
			return this;
		}
		
		public Set<AssetType> specialises() {
			return supertypes;
		}
	}
}
