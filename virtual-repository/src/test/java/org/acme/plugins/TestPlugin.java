package org.acme.plugins;

import static java.util.Collections.*;
import static org.acme.Mocks.*;

import java.util.Collection;

import org.virtualrepository.Repository;
import org.virtualrepository.spi.VirtualPlugin;



public class TestPlugin implements VirtualPlugin {

	@Override
	public Collection<Repository> repositories() {
		return singleton(repo().get());
	}
}
