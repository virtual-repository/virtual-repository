package org.virtualrepository;

import static lombok.AccessLevel.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import smallgears.api.properties.Properties;



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
	Repository repository();
	
	
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
		private Repository repository;
		
		private Properties properties = Properties.props();

		/**
		 * Client-facing, for publication in repositories that take identifiers from clients.
		 */ 
		protected Private(AssetType type, String id, String name, @NonNull Repository repo) {
			this(type, id, name);
			repository(repo);
		}
		
		/**
		 * Client-facing, for publication in repositories that generate identifiers.
		 */
		protected Private(AssetType type, String name, Repository repo) {
			this(type,"unassigned", name, repo);
		}
	}
}
