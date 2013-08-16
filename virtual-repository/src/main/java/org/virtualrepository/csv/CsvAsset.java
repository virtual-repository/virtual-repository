package org.virtualrepository.csv;

import static java.util.Arrays.*;
import static org.virtualrepository.Utils.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.virtualrepository.Asset;
import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.Type;
import org.virtualrepository.tabular.Column;

/**
 * Partial implementation of an {@link Asset} available in the CSV format.
 * 
 * @author Fabio Simeoni
 * 
 */
public class CsvAsset extends AbstractAsset {

	public static final char defaultDelimiter = ',';
	public static final char defaultQuote = '"';
	public static final String defaultEncoding = "UTF-8";
	public static final boolean defaultHeader = false;
	public static final long defaultRows = Long.MAX_VALUE;
	
	
	/**
	 * The generic type of {@link CsvAsset}s.
	 */
	public static final Type<CsvAsset> type = new CsvGenericType();
	
	private char delimiter = defaultDelimiter;
	private char quote= defaultQuote;
	private boolean header = defaultHeader;
	private String encoding = defaultEncoding;
	
	private List<Column> columns = new ArrayList<Column>();
	
	private long rows = defaultRows;
	
	/**
	 * Creates an instance with a given type, identifier, name, and properties.
	 *  <p>
	 * Inherit as a plugin-facing constructor for asset discovery and retrieval purposes.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	protected <T extends CsvAsset> CsvAsset(Type<T> type, String id, String name, Property ... properties) {
		super(type,id, name, properties);

	}
	
	/**
	 * Creates an instance with a given identifier, name, and properties.
	 * <p>
	 * A plugin-facing constructor for asset discovery and retrieval.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	public <T extends CsvAsset> CsvAsset(String id, String name, Property ... properties) {
		this(type,id, name,properties);
	}

	/**
	 * Creates an instance with a given type, identifier, name, target service, and properties.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication with services that allow client-defined identifiers.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	protected <T extends CsvAsset> CsvAsset(Type<T> type,String id, String name, RepositoryService service, Property ... properties) {
		super(type,id, name,service,properties);
	}
	
	/**
	 * Creates an instance with a given type, name, target service, and properties.
	 * <p>
	 * Inherit as a client-facing constructor for asset publication with services that do now allow client-defined
	 * identifiers, or else that force services to generate identifiers.
	 * 
	 * @param type the type
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	protected <T extends CsvAsset> CsvAsset(Type<T> type,String name, RepositoryService service, Property ... properties) {
		super(type,name,service,properties);
	}
	
	/**
	 * Creates an instance with a given name, target service, and properties.
	 * <p>
	 * Use for asset publication with services that do now allow client-defined
	 * identifiers, or else that force services to generate identifiers.
	 * 
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	//client-facing constructor for publication
	public <T extends CsvAsset> CsvAsset(String name, RepositoryService service, Property ... properties) {
		super(type,name,service,properties);
	}
	
	
	/**
	 * Creates an instance with a given identifier, name, target service, and properties.
	 * <p>
	 * Use for asset publication with services that allow client-defined identifiers.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param service the target service
	 * @param properties the properties
	 * 
	 * */
	//client-facing constructor for 
	public <T extends CsvAsset> CsvAsset(String name, String id, RepositoryService service, Property ... properties) {
		super(type,id, name,service,properties);
	}

	/**
	 * Returns the delimiter character used in the content of this asset (by default a comma, <code>\u002C</code>).
	 * 
	 * @return the delimiter character
	 */
	public char delimiter() {
		return delimiter;
	}

	/**
	 * Sets the delimiter character used in the content of this asset, overwriting the default (a comma,
	 * <code>\u002C</code>).
	 * 
	 * @param delimiter the delimiter character
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter=delimiter;
	}

	/**
	 * Returns the quote character used in the content of this asset (by default a quotation mark, <code>\u0022</code>).
	 * 
	 * @return the quote character
	 */
	public char quote() {
		return quote;
	}

	/**
	 * Sets the quote character used in the content of this asset, overriding the default (a quotation mark,
	 * <code>\u0022</code>).
	 * 
	 * @param quote the quote character
	 */
	public void setQuote(char quote) {
		this.quote=quote;
	}

	/**
	 * Indicates whether the content of this asset has a header row (by default <code>false</code>).
	 * 
	 * @return <code>true</code> if the content of this asset has a header row
	 */
	public boolean hasHeader() {
		return header;
	}

	/**
	 * Indicates whether the content of this asset has a header row, overriding the default (<code>false</code>).
	 * 
	 * @param header <code>true</code> if the content of this asset has a header row
	 */
	public void hasHeader(boolean header) {
		this.header=header;
	}

	/**
	 * Returns the encoding of the content of this asset (by default <code>UTF-8</code>).
	 * 
	 * @return the encoding of the content of this asset
	 */
	public Charset encoding() {
		return Charset.forName(encoding);
	}

	/**
	 * Sets the encoding of the content of this asset, overriding the default (<code>UTF-8</code>).
	 * 
	 * @param encoding the encoding
	 */
	public void setEncoding(Charset encoding) {
		notNull("encoding", encoding);
		this.encoding = encoding.name();
	}

	/**
	 * Returns the columns of this asset.
	 * 
	 * @return the columns, in an <em>immutable</emp> collection
	 */
	public List<Column> columns() {
		return columns;

	}

	/**
	 * Sets the number of rows in the content of this asset.
	 * 
	 * @param rows the number of rows
	 */
	public void setRows(long rows) {

		notNull("rows",rows);
		this.rows=rows;
	}
	
	/**
	 * Returns the number of rows in the content of this asset.
	 * 
	 * @return the number of rows
	 */
	public Long rows() {

		return rows;

	}

	/**
	 * Sets the columns of this asset.
	 * 
	 * @param cols the columns
	 */
	public void setColumns(Column... columns) {

		notNull("columns", columns);

		this.columns=new ArrayList<Column>(asList(columns));
	}



	@Override
	public String toString() {
		final int maxLen = 100;
		return type().name() + " [delimiter=" + delimiter + ", quote=" + quote + ", header=" + header + ", encoding="
				+ encoding + ", columns="
				+ (columns != null ? columns.subList(0, Math.min(columns.size(), maxLen)) : null) + ", rows=" + rows
				+ ", id()=" + id() + ", type()=" + type() + ", service()=" + service() + ", name()=" + name()
				+ ", properties()=" + properties() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + delimiter;
		result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + (header ? 1231 : 1237);
		result = prime * result + quote;
		result = prime * result + (int) (rows ^ (rows >>> 32));
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
		CsvAsset other = (CsvAsset) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (delimiter != other.delimiter)
			return false;
		if (encoding == null) {
			if (other.encoding != null)
				return false;
		} else if (!encoding.equals(other.encoding))
			return false;
		if (header != other.header)
			return false;
		if (quote != other.quote)
			return false;
		if (rows != other.rows)
			return false;
		return true;
	}

	
	
}
