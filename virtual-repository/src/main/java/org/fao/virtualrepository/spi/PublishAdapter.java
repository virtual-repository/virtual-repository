package org.fao.virtualrepository.spi;

import static org.fao.virtualrepository.Utils.*;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * A {@link Publisher} that adapts the bound API of another {@link Publisher}.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type the assets handled by the publisher
 * @param <A1> the bound API of the publisher
 * @param <A2> the bound API of the adapter
 */
public class PublishAdapter<T extends Asset,A1,A2> implements Publisher<T, A2> {
	
	/**
	 * Adapts a {@link Publisher} with a given {@link Transform}.
	 * @param publisher the publisher
	 * @param transform the transform
	 * @return the adapted publisher
	 */
	public static <T extends Asset,A1,A2> Publisher<T,A2> adapt(Publisher<T,A1> publisher,Transform<T,A2,A1> transform) {
		
		notNull("publisher",publisher);
		notNull("transform",transform);
		
		return new PublishAdapter<T, A1, A2>(publisher, transform);
	}
	
	private final Publisher<T,A1> publisher;
	private final Transform<T,A2,A1> transform;
	
	/**
	 * Creates an instance with a given {@link Publisher} and a given {@link Transform}.
	 * @param publisher the publisher
	 * @param transform the transform
	 */
	private PublishAdapter(Publisher<T,A1> publisher, Transform<T,A2,A1> transform) {
		this.publisher=publisher;
		this.transform=transform;
	}

	@Override
	public AssetType<T> type() {
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
