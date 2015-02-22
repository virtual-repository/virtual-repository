package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;

import java.util.Collection;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.VirtualPlugin;



public class TestPlugin implements VirtualPlugin {

	@Override
	public Collection<RepositoryService> services() {
		return singleton(aService().get());
	}
}
