package org.virtualrepository;

import api.tabular.Properties;



/**
 * Describes a data asset held - or bound to be held - in a repository.
 */
public interface Asset {

	/**
	 * The identifier of this asset, globally unique.
	 */
	String id();

	/**
	 * The name of this asset, unique within this asset's repository.
	 */
	String name();

	/**
	 * The properties of this asset.
	 */
	Properties properties();
	
	/**
	 * The type of this asset.
	 */
	AssetType type();

	/**
	 * The repository where this asset is held, or bound to be.
	 */
	RepositoryService service();
}
