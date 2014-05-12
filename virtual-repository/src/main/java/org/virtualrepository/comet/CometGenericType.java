package org.virtualrepository.comet;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class CometGenericType extends AbstractType<CometAsset> {
	
	private static final String name = "comet/generic";
	
	public CometGenericType() {
		super(name);
	}
}
