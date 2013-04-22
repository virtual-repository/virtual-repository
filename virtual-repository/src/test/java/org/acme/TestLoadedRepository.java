package org.acme;

import javax.xml.namespace.QName;

import org.fao.virtualrepository.spi.RepositoryDescription;


public class TestLoadedRepository extends TestRepository {
	
	public TestLoadedRepository() {
		super(new RepositoryDescription(new QName("test-loaded-repo")));
	}
	

}
