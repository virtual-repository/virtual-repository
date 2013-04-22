package org.fao.virtualrepository.spi;

import java.util.List;

public interface Repository {

	String name();

	List<? extends Reader> readers();

	List<? extends Writer> writers();

}
