package org.virtualrepository.impl;

import static api.tabular.Properties.*;
import static org.virtualrepository.Utils.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.AssetType;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.ServiceProxy;

import api.tabular.Properties;

/**
 * A repository with ingestion and dissemination APIs.
 * <p>
 * Wraps plugin-specific {@link ServiceProxy}s.
 */
@RequiredArgsConstructor
@Data
public final class DefaultRepositoryService implements RepositoryService {

	@NonNull
	private final String name;
	
	@NonNull
	private final ServiceProxy proxy;
	
	private final Properties properties = props();
	
	@Override
	public boolean takes(AssetType ... types) {
		return supports(proxy.publishers(),types);
		
	}
	
	@Override
	public Set<AssetType> typesTaken() {
		return supported(proxy.publishers());
	}
	
	@Override
	public boolean returns(AssetType ... types) {
		return supports(proxy.importers(),types);
	}
	
	@Override
	public Set<AssetType> typesReturned() {
		return supported(proxy.importers());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean supports(List<? extends Accessor<?,?>> accessors, AssetType... types) {
		
		notNull("asset types",types);
		
		for (AssetType supported : supported(accessors))
			for (AssetType type : types)
				if (supported.equals(type)) 
					return true;

		return false;
	}
	
	private Set<AssetType> supported(List<? extends Accessor<?,?>> accessors) {
		
		Set<AssetType> types = new HashSet<AssetType>();
		for (Accessor<?,?> accessor : accessors)
			types.add(accessor.type());

		return types;
	}


	
}
