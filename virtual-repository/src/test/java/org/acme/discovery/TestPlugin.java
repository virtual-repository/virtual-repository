package org.acme.discovery;

import static java.util.Collections.*;
import static org.acme.Mocks.*;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.Repository;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.VirtualPlugin;


@Slf4j(topic="test-plugin")
public class TestPlugin implements VirtualPlugin, Lifecycle {

	@Override
	public Collection<Repository> repositories() {
		return singleton(repo().get());
	}
	
	@Override
	public void init() throws Exception {
		log.info("initialising");
	}
	
	@Override
	public void shutdown() throws Exception {
		log.info("shutting down");
	}
}
