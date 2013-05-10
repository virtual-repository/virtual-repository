package org.acme;

import static java.util.Collections.*;
import static org.acme.TestMocks.*;

import java.util.Collection;

import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Plugin;



public class TestPlugin implements Plugin {

	@Override
	public Collection<RepositoryService> services() {
		return singleton(aService().get());
	}
}
