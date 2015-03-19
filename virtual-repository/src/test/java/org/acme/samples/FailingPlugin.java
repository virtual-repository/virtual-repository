package org.acme.samples;

import smallgears.virtualrepository.spi.Lifecycle;


public class FailingPlugin extends TestPlugin implements Lifecycle {

	@Override
	public void init() throws Exception {
		throw new Exception("test failure");
	}
}
