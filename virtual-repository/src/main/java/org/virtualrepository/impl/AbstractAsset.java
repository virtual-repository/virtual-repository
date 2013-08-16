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

	private AssetType type;

	private String id;

	private String name;

	private RepositoryService service;

	/**
	 * Creates an instance with a given type, identifier, name, and properties.
	 *  <p>
	 * Inherit as a plugin-facing constructor for asset discovery and retrieval.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	protected AbstractAsset(AssetType type, String id, String name, Property... properties) {

		notNull("type", type);
		this.type = type;

		notNull("asset identifier", id);
		this.id = id;

		notNull("asset name", id);
		this.name = name;

		this.properties().add(properties);

	}

	/**
	 * Creates an instance with a given type,identifier, name, target repository service, and properties.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication with services that allow client-defined identifiers.
	 * 
	 * @param type the type
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
	 * Creates an instance with a given type, name, target repository service, and properties.
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
		return type;
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
		return type().name() + " [" + id() + "," + name() + (properties().isEmpty() ? "" : ", " + properties()) + ","
				+ service() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}



}
