package org.virtualrepository.spi;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * A reader that adapts another reader to return asset content under a different API.
 */
@RequiredArgsConstructor(staticName="adapt")
public class ReaderAdapter<T extends Asset,A1,A2> implements VirtualReader<T, A2> {
	
	@NonNull
	private final VirtualReader<T,A1> reader;
	
	@NonNull
	private final Transform<T,A1,A2> transform;

	@Override
	public AssetType type() {
		return reader.type();
	}

	@Override
	public Class<A2> api() {
		return transform.targetApi();
	}

	@Override
	public A2 retrieve(T asset) throws Exception {
		return transform.apply(asset,reader.retrieve(asset));
	}
}
