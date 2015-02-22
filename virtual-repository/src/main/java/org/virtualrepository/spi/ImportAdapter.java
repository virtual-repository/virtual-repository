package org.virtualrepository.spi;

import static org.virtualrepository.Utils.*;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * A {@link Importer} that adapts the bound API of another {@link Importer}.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type the assets handled by the importer
 * @param <A1> the bound API of the importer
 * @param <A2> the bound API of the adapter
 */
public class ImportAdapter<T extends Asset,A1,A2> implements Importer<T, A2> {
	
	/**
	 * Wraps a {@link Importer} with a given {@link Transform}
	 * @param importer the importer
	 * @param transform the transform
	 * @return the adapted importer
	 */
	public static <T extends Asset,A1,A2> Importer<T,A2> adapt(Importer<T,A1> importer,Transform<T,A1,A2> transform) {
		
		notNull("importer",importer);
		notNull("transform",transform);
		
		return new ImportAdapter<T, A1, A2>(importer, transform);
	}
	
	private final Importer<T,A1> importer;
	private final Transform<T,A1,A2> transform;
	
	/**
	 * Creates an instance with a given {@link Importer} and a given {@link Transform}.
	 * @param importer the importer
	 * @param transform the transform
	 */
	private ImportAdapter(Importer<T,A1> importer, Transform<T,A1,A2> transform) {
		this.importer=importer;
		this.transform=transform;
	}

	@Override
	public AssetType type() {
		return importer.type();
	}

	@Override
	public Class<A2> api() {
		return transform.outputAPI();
	}

	@Override
	public A2 retrieve(T asset) throws Exception {
		return transform.apply(asset,importer.retrieve(asset));
	}
}
