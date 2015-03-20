package smallgears.virtualrepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import smallgears.api.properties.Properties;



/**
 * Describes a data asset disseminated or to be ingested by a repository.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Asset {
		
	/**
	 * The type of this asset.
	 */
	@NonNull
	private AssetType type;

	/**
	 * The identifier of this asset, globally unique.
	 */
	@NonNull
	private String id;

	/**
	 * The name of this asset, unique within this asset's repository.
	 */
	@NonNull
	private String name;
	
	/**
	 * The repository which disseminated or will ingest this asset.
	 */
	@Setter //lazily set
	private Repository repository;
	
	/**
	 * The properties of this asset.
	 */
	private Properties properties = Properties.props();

	/**
	 * Client-facing, for publication in repositories that take identifiers from clients.
	 */ 
	public Asset(AssetType type, String id, String name, @NonNull Repository repo) {
		this(type, id, name);
		repository(repo);
	}
	
	/**
	 * Client-facing, for publication in repositories that generate identifiers.
	 */
	public Asset(AssetType type, String name, Repository repo) {
		this(type,"unassigned", name, repo);
	}
}
