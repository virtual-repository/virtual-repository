package org.virtualrepository.spi;

import static org.virtualrepository.Utils.*;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * A {@link VirtualWriter} that adapts the bound API of another {@link VirtualWriter}.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type the assets handled by the publisher
 * @param <A1> the bound API of the publisher
 * @param <A2> the bound API of the adapter
 */
public class PublishAdapter<T extends Asset,A1,A2> implements VirtualWriter<T, A2> {
	
	/**
	 * Adapts a {@link VirtualWriter} with a given {@link Transform}.
	 * @param publisher the publisher
	 * @param transform the transform
	 * @return the adapted publisher
	 */
	public static <T extends Asset,A1,A2> VirtualWriter<T,A2> adapt(VirtualWriter<T,A1> publisher,Transform<T,A2,A1> transform) {
		
		notNull("publisher",publisher);
		notNull("transform",transform);
		
		return new PublishAdapter<T, A1, A2>(publisher, transform);
	}
	
	private final VirtualWriter<T,A1> publisher;
	private final Transform<T,A2,A1> transform;
	
	/**
	 * Creates an instance with a given {@link VirtualWriter} and a given {@link Transform}.
	 * @param publisher the publisher
	 * @param transform the transform
	 */
	private PublishAdapter(VirtualWriter<T,A1> publisher, Transform<T,A2,A1> transform) {
		this.publisher=publisher;
		this.transform=transform;
	}

	@Override
	public AssetType type() {
		return publisher.type();
	}

	@Override
	public Class<A2> api() {
		return transform.inputAPI();
	}

	@Override
	public void publish(T asset, A2 content) throws Exception {
		
		A1 transformed = transform.apply(asset,content);
		
		publisher.publish(asset,transformed);
	};
}
