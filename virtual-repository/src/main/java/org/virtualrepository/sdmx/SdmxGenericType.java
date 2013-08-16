package org.virtualrepository.sdmx;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class SdmxGenericType extends AbstractType<SdmxAsset> {
	
	private static final String name = "sdmx/generic";
	
	public SdmxGenericType() {
		super(name);
	}
}
