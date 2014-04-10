package org.virtualrepository.fmf;

import javax.xml.bind.annotation.XmlRootElement;

import org.virtualrepository.impl.AbstractType;

@XmlRootElement(name="type")
public class FmfGenericType extends AbstractType<FmfAsset> {
	
	private static final String name = "fmf/generic";
	
	public FmfGenericType() {
		super(name);
	}
}
