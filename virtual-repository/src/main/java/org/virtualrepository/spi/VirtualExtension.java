package org.virtualrepository.spi;


/**
 * An extension's entry point.
 *
 */
public interface VirtualExtension {

	String name();
	
	Iterable<Transform<?,?>> transforms();
}
