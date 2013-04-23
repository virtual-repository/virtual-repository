package org.fao.virtualrepository.processor;

import java.util.HashMap;
import java.util.Map;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.AssetType;

public class Processors {

	private static final Map<Class<?>, AssetProcessor<?>> processors = new HashMap<Class<?>,AssetProcessor<?>>();
	
	
	public static <A extends Asset> void add(AssetType<A> type, AssetProcessor<A> processor) {
		processors.put(type.getClass(),processor);
	}
	
	public static void process(Asset asset) {
		
		@SuppressWarnings("unchecked")
		AssetProcessor<Asset> processor = (AssetProcessor<Asset>) processors.get(asset.type().getClass());
		
		if (processor==null)
			throw new IllegalStateException("no processor registered for "+asset.type());
		
		processor.process(asset);
	
	}
	
}
