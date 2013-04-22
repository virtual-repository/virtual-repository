package org.fao.virtualrepository.spi;

import java.util.List;

import javax.xml.namespace.QName;

public interface Repository {

	QName name();

	List<? extends Reader<?>> readers();

	List<? extends Writer<?>> writers();

}
