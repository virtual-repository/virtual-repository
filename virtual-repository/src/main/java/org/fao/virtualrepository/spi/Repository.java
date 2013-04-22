package org.fao.virtualrepository.spi;

import java.util.List;

public interface Repository {

	RepositoryDescription description();

	List<? extends Reader> readers();

	List<? extends Writer> writers();

}
