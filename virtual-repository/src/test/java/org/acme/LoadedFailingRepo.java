package org.acme;

import org.virtualrepository.spi.Lifecycle;


public class LoadedFailingRepo extends TestRepo implements Lifecycle {

	@Override
	public void init() throws Exception {
		throw new Exception("test failure");
	}
}