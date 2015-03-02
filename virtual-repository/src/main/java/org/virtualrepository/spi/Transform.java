package org.virtualrepository.spi;

import org.virtualrepository.Asset;

/**
 * Transforms asset content from one API (the source) to another (the target).
 */
public interface Transform<A extends Asset, IN, OUT> {

	/**
	 * Transforms the content of an asset.
	 */
	OUT apply(A asset, IN input) throws Exception;

	/**
	 * The source API.
	 */
	Class<IN> sourceAPI();

	/**
	 * The target type.
	 */
	Class<OUT> targetAPI();
	
	/**
	 * Adapts a given reader with this transform.
	 */
	default VirtualReader<A,OUT> apply(VirtualReader<A,IN> reader) {
		
		return ReaderAdapter.adapt(reader,this);
	}
	
	/**
	 * Adapts a given writer with this transform.
	 */
	default VirtualWriter<A,IN> apply(VirtualWriter<A,OUT> writer) {
		
		return WriterAdapter.adapt(writer,this);
	
	}
}
