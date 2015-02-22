package org.virtualrepository;

import static lombok.AccessLevel.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	@RequiredArgsConstructor(access=PROTECTED)
	@Getter
	@EqualsAndHashCode
	public class Private implements Asset {
		
		@NonNull
		private final AssetType type;

		@NonNull
		private final String id;

		@NonNull
		private final String name;
		
		@Setter //lazily set
		private RepositoryService service;
		
		private Properties properties = Properties.props();

		/**
		 * Client-facing, for publication in repositories that take identifiers from clients.
		 */ 
		protected Private(AssetType type, String id, String name, @NonNull RepositoryService service) {
			this(type, id, name);
			service(service);
		}
		
		/**
		 * Client-facing, for publication in repositories that generate identifiers.
		 */
		protected Private(AssetType type, String name, RepositoryService service) {
			this(type,"unassigned", name, service);
		}
	}
}
