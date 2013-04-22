package org.fao.virtualrepository.processor;

import org.fao.virtualrepository.Asset;

public interface AssetProcessor<A extends Asset> {

	void process(A asset);
}
