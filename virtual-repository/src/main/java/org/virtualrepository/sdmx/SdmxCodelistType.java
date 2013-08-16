package org.virtualrepository.sdmx;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class SdmxCodelistType extends AbstractType<SdmxCodelist> {
	
	private static final String name = "sdmx/codelist";
	
	public SdmxCodelistType() {
		super(name);
	}
}
