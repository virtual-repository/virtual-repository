package org.fao.virtualrepository.spi;

import static org.fao.virtualrepository.Utils.*;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

/**
 * A {@link Reader} that wraps another {@link Reader} transforming its bound API.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type the assets discovered and retrieved by the wrapper
 * @param <A1> the bound API of the original reader
 * @param <A2> the bound API of the wrapper
 */
public class ReaderWrapper<T extends Asset,A1,A2> implements Reader<T, A2> {
	
	/**
	 * Wraps a {@link Reader} with a given {@link Transform}
	 * @param reader the reader
	 * @param transform the transform
	 * @return the {@link ReaderWrapper}
	 */
	public static <T extends Asset,A1,A2> Reader<T,A2> wrap(Reader<T,A1> reader,Transform<A1,A2> transform) {
		
		notNull("reader",reader);
		notNull("transform",transform);
		
		return new ReaderWrapper<T, A1, A2>(reader, transform);
	}
	
	private final Reader<T,A1> reader;
	private final Transform<A1,A2> transform;
	
	/**
	 * Creates an instance with a given {@link Reader} and a given {@link Transform}.
	 * @param reader the reader
	 * @param transform the transform
	 */
	public ReaderWrapper(Reader<T,A1> reader, Transform<A1,A2> transform) {
		this.reader=reader;
		this.transform=transform;
	}

	@Override
	public AssetType<? extends T> type() {
		return reader.type();
	}

	@Override
	public Class<? extends A2> api() {
		return transform.api();
	}

	@Override
	public Iterable<? extends T> find() {
		return reader.find();
	}

	@Override
	public A2 fetch(T asset) {
		return transform.transform(reader.fetch(asset));
	}
}
