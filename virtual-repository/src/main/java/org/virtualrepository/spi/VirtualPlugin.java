package org.virtualrepository.spi;

import java.util.Collection;

import org.virtualrepository.Repository;


/**
 * A plugin's entry point.
 *
 */
public interface VirtualPlugin {

	Collection<Repository> repositories();
}
