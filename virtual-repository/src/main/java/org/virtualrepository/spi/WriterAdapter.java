package org.virtualrepository.spi;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * A writer that adapts another writer to take asset content under a different API.
 */
@RequiredArgsConstructor(staticName="adapt")
public class WriterAdapter<T extends Asset,A1,A2> implements VirtualWriter<T, A2> {
	
	@NonNull
	private final VirtualWriter<T,A1> writer;
	@NonNull
	private final Transform<T,A2,A1> transform;
	
	@Override
	public AssetType type() {
		return writer.type();
	}

	@Override
	public Class<A2> api() {
		return transform.inputAPI();
	}

	@Override
	public void publish(T asset, A2 content) throws Exception {
		
		A1 transformed = transform.apply(asset,content);
		
		writer.publish(asset,transformed);
	};
}
