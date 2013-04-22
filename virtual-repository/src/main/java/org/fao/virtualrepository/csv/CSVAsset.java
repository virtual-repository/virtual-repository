package org.fao.virtualrepository.csv;

import org.fao.virtualrepository.Asset;

public class CSVAsset implements Asset {

	final String id;
	final String name;
	
	public CSVAsset(String id, String name) {
		this.id=id;
		this.name=name;
	}
	
	@Override
	public String id() {
		return id;
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
		return true;
	}
	
	
}
