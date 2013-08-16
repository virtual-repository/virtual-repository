package org.virtualrepository.csv;

import static org.virtualrepository.Utils.*;

import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.Type;

/**
 * A {@link CsvAsset} that represents a codelist.
 * 
 * @author Fabio Simeoni
 *
 */
public final class CsvCodelist extends CsvAsset {
	
	private int codeColumn;
	
	/**
	 * The type of {@link CsvCodelist}s.
	 */
	public static final Type<CsvCodelist> type = new CsvCodelistType();

	
	/**
	 * Creates an instance with a given identifier, name, code column index, and properties.
	 * <p>
	 * Plugin-facing constructor for asset discovery and retrieval purposes.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param codeColumnIndex the index of the column that contains the codes of this list
	 * @param properties the properties
	 */ 
	public CsvCodelist(String id, String name, int codeColumnIndex, Property ... properties) {
		
		super(type,id,name, properties);
		setCodeColumn(codeColumnIndex);
	}
	
	/**
	 * Creates an instance with a given name, target service, and properties.
	 * <p>
	 * Use for asset publication with services that do now allow client-defined
	 * identifiers, or else that force services to generate identifiers.
	 * 
	 * @param name the name
	 * @param codeColumnIndex the index of the column that contains the codes of this list
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	public <T extends CsvAsset> CsvCodelist(String name, int codeColumnIndex, RepositoryService service, Property ... properties) {
		super(type,name,service,properties);
		setCodeColumn(codeColumnIndex);
	}
	
	
	/**
	 * Creates an instance with a given identifier, name, target service, and properties.
	 * <p>
	 * Use for asset publication with services that allow client-defined identifiers.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param codeColumnIndex the index of the column that contains the codes of this list
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	public <T extends CsvAsset> CsvCodelist(String id, String name, int codeColumnIndex, RepositoryService service, Property ... properties) {
		super(type,id, name,service,properties);
		
		setCodeColumn(codeColumnIndex);
	}
	
	/**
	 * Returns the index of the column that contains the codes of this list.
	 * 
	 * @return the code column index
	 */
	public int codeColumn() {

		return codeColumn;

	}
	
	
	/**
	 * Sets the index of the column that contains the codes of this list.
	 * 
	 * @param codeColumnIndex the code column index
	 */
	protected void setCodeColumn(int codeColumnIndex) {

		notNull("code column index",codeColumnIndex);
		
		this.codeColumn=codeColumnIndex;

	}
	
	

	@Override
	public String toString() {
		final int maxLen = 100;
		return "CsvCodelist [codeColumn=" + codeColumn + ", delimiter()=" + delimiter() + ", quote()=" + quote()
				+ ", hasHeader()=" + hasHeader() + ", encoding()=" + encoding() + ", columns()="
				+ (columns() != null ? columns().subList(0, Math.min(columns().size(), maxLen)) : null) + ", rows()="
				+ rows() + ", id()=" + id() + ", type()=" + type() + ", service()=" + service() + ", name()=" + name()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + codeColumn;
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
		CsvCodelist other = (CsvCodelist) obj;
		if (codeColumn != other.codeColumn)
			return false;
		return true;
	}
	
	
}
