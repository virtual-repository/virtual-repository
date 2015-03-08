package org.virtualrepository.spi;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * A writer that adapts another writer to take asset content under a different API.
 */
@RequiredArgsConstructor(staticName="adapt")
public class WriterAdapter<A1,A2> implements VirtualWriter<A2> {
	
	@NonNull
	private final VirtualWriter<A1> writer;
	@NonNull
	private final Transform<A2,A1> transform;
	
	@Override
	public AssetType type() {
		return writer.type();
	}

	@Override
	public Class<A2> api() {
		return transform.sourceApi();
	}

	@Override
	public void publish(Asset asset, A2 content) throws Exception {
		
		writer.publish(asset,transform.apply(asset,content));
	};
}
