package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;

import java.util.Collection;

import org.virtualrepository.spi.Plugin;
import org.virtualrepository.spi.RepositoryService;



public class TestPlugin implements Plugin {

	@Override
	public Collection<RepositoryService> services() {
		return singleton(aService().get());
	}
}
