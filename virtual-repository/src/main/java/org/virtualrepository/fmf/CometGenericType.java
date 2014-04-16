package org.virtualrepository.fmf;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class CometGenericType extends AbstractType<CometAsset> {
	
	private static final String name = "fmf/generic";
	
	public CometGenericType() {
		super(name);
	}
}
