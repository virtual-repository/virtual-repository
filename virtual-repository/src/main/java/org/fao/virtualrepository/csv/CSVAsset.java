package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.Asset;
import org.fao.virtualrepository.spi.Repository;

public class CSVAsset implements Asset {

	final String id;
	final String name;
	final Repository origin;
	
	public CSVAsset(String id, String name, Repository origin) {
		this.id=id;
		this.name=name;
		this.origin=origin;
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@Override
	public Repository origin() {
		return origin;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public CSV type() {
		return new CSV();
	}

	@Override
	public String toString() {
		return "CSVAsset [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSVAsset other = (CSVAsset) obj;
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
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}
	
}
