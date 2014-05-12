package org.virtualrepository.impl;

import static org.virtualrepository.Utils.*;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.MutableAsset;

/**
 * Partial {@link Asset} implementation.
 * 
 * @author Fabio Simeoni
 * 
 * @see Asset
 */
public abstract class AbstractAsset extends PropertyHolder implements MutableAsset {

	//'type' is used in public static constants in subclasses. we alias the field to avoid shadowing problems
	// with certain tools that analyse the hierarchy reflectively (e.g. xstream).
	private AssetType _type;

	private String id;

	private String name;
	
	private String version;

	private RepositoryService service;

	/**
	 * Creates an instance with a given _type, identifier, name, and properties.
	 *  <p>
	 * Inherit as a plugin-facing constructor for asset discovery and retrieval.
	 * 
	 * @param _type the _type
	 * @param id the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	protected AbstractAsset(AssetType type, String id, String name, Property... properties) {

		notNull("_type", type);
		this._type = type;

		notNull("asset identifier", id);
		this.id = id;

		notNull("asset name", id);
		this.name = name;

		this.properties().add(properties);

	}
	
	@Override
	public String version() {
		return version;
	}
	
	/**
	 * Sets the version of this asset
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Creates an instance with a given _type,identifier, name, target repository service, and properties.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication with services that allow client-defined identifiers.
	 * 
	 * @param _type the _type
	 * @param id the identifier
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 */
	protected AbstractAsset(AssetType type, String id, String name, RepositoryService service, Property... properties) {

		this(type, id, name, properties);
		notNull("target service", service);
		setService(service);

	}

	/**
	 * Creates an instance with a given _type, name, target repository service, and properties.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication with services that do now allow client-defined
	 * identifiers, or else that force services to generate identifiers.
	 * 
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 */
	protected AbstractAsset(AssetType type, String name, RepositoryService service, Property... properties) {

		this(type,"unassigned", name, service, properties);

	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public AssetType type() {
		return _type;
	}

	@Override
	public RepositoryService service() {
		return service;
	}

	@Override
	public void setService(RepositoryService service) {

		notNull("asset service", id);
		this.service = service;

		this.service = service;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return type().name() + " [" + id() + "," + name() + (version()==null?"":","+version) + (properties().isEmpty() ? "" : ", " + properties()) + ","
				+ service() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAsset other = (AbstractAsset) obj;
		if (_type == null) {
			if (other._type != null)
				return false;
		} else if (!_type.equals(other._type))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

 



}
