package smallgears.virtualrepository.spi;


/**
 * An extension's entry point.
 *
 */
public interface VirtualExtension extends Lifecycle {

	String name();
	
	Iterable<Transform<?,?>> transforms();
}
