package org.virtualrepository.csv;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.virtualrepository.Utils.*;

import java.nio.charset.Charset;
import java.util.List;

import org.virtualrepository.Asset;
import org.virtualrepository.Property;
import org.virtualrepository.impl.AbstractAsset;
import org.virtualrepository.impl.AbstractType;
import org.virtualrepository.impl.Type;
import org.virtualrepository.tabular.Column;

/**
 * Partial implementation of an {@link Asset} available in the CSV format.
 * 
 * @author Fabio Simeoni
 * 
 */
public class CsvAsset extends AbstractAsset {

	// constants
	public static final String delimiter = "delimiter";
	public static final String quote = "quote";
	public static final String header = "header";
	public static final String encoding = "encoding";
	public static final String columns = "columns";
	public static final String rows = "rows";

	public static final char defaultDelimiter = ',';
	public static final char defaultQuote = '"';
	public static final Charset defaultEncoding = Charset.forName("UTF-8");
	public static final boolean defaultHeader = false;
	public static final long defaultRows = Long.MAX_VALUE;
	
	
	private static final String name = "csv/generic";
	
	/**
	 * The generic type of {@link CsvAsset}s.
	 */
	public static final Type<CsvAsset> type = new AbstractType<CsvAsset>(name) {};

	 /**
	 * Creates an instance with a given type, identifier, and name.
	 * 
	 * @param type the type
	 * @param name the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	protected <T extends CsvAsset> CsvAsset(Type<T> type, String id, String name) {
		super(type,id, name, defaultProperties());

	}
	
	/**
	 * Creates an instance with the generic {@link #type}, a given identifier, and name.
	 * 
	 * @param name the identifier
	 * @param name the name
	 * @param properties the properties
	 */
	public <T extends CsvAsset> CsvAsset(String id, String name) {
		this(type,id, name);
	}

	/**
	 * Returns the delimiter character used in the content of this asset (by default a comma, <code>\u002C</code>).
	 * 
	 * @return the delimiter character
	 */
	public char delimiter() {
		return properties().lookup(delimiter).value(Character.class);
	}

	/**
	 * Sets the delimiter character used in the content of this asset, overwriting the default (a comma,
	 * <code>\u002C</code>).
	 * 
	 * @param delimiter the delimiter character
	 */
	public void setDelimiter(char delimiter) {
		properties().add(delimiter(delimiter));
	}

	/**
	 * Returns the quote character used in the content of this asset (by default a quotation mark, <code>\u0022</code>).
	 * 
	 * @return the quote character
	 */
	public char quote() {
		return properties().lookup(quote).value(Character.class);
	}

	/**
	 * Sets the quote character used in the content of this asset, overriding the default (a quotation mark,
	 * <code>\u0022</code>).
	 * 
	 * @param quote the quote character
	 */
	public void setQuote(char quote) {
		properties().add(quote(quote));
	}

	/**
	 * Indicates whether the content of this asset has a header row (by default <code>false</code>).
	 * 
	 * @return <code>true</code> if the content of this asset has a header row
	 */
	public boolean hasHeader() {
		return properties().lookup(header).value(Boolean.class);
	}

	/**
	 * Indicates whether the content of this asset has a header row, overriding the default (<code>false</code>).
	 * 
	 * @param header <code>true</code> if the content of this asset has a header row
	 */
	public void hasHeader(boolean header) {
		properties().add(header(header));
	}

	/**
	 * Returns the encoding of the content of this asset (by default <code>UTF-8</code>).
	 * 
	 * @return the encoding of the content of this asset
	 */
	public Charset encoding() {
		return properties().lookup(encoding).value(Charset.class);
	}

	/**
	 * Sets the encoding of the content of this asset, overriding the default (<code>UTF-8</code>).
	 * 
	 * @param encoding the encoding
	 */
	public void setEncoding(Charset encoding) {
		notNull("encoding", encoding);
		properties().add(encoding(encoding));
	}

	/**
	 * Returns the columns of this asset.
	 * 
	 * @return the columns, in an <em>immutable</emp> collection
	 */
	@SuppressWarnings("unchecked")
	public List<Column> columns() {

		return (List<Column>) properties().lookup(columns).value(List.class);

	}

	/**
	 * Sets the number of rows in the content of this asset.
	 * 
	 * @param rows the number of rows
	 */
	public void setRows(long rows) {

		notNull("rows",rows);

		properties().add(rows(rows));
	}
	
	/**
	 * Returns the number of rows in the content of this asset.
	 * 
	 * @return the number of rows
	 */
	public Long rows() {

		return properties().lookup(rows).value(Long.class);

	}

	/**
	 * Sets the columns of this asset.
	 * 
	 * @param cols the columns
	 */
	public void setColumns(Column... cols) {

		notNull("columns", cols);

		properties().add(columns(cols));
	}

	// helpers

	private static Property delimiter(char d) {
		return new Property(delimiter, d, "column delimiter character");
	}

	private static Property quote(char q) {
		return new Property(quote, q, "value quote character");
	}

	private static Property header(boolean h) {
		return new Property(header, h, "flags existence of a header row");
	}

	private static Property encoding(Charset c) {
		return new Property(encoding, c, "charset for content encoding");
	}

	private static Property columns(Column... cols) {
		return new Property(columns, unmodifiableList(asList(cols)), "columns");
	}

	private static Property rows(long r) {
		return new Property(rows, r, "number of rows");
	}
	

	private static Property[] defaultProperties() {

		return new Property[] { columns(new Column[0]), delimiter(defaultDelimiter), quote(defaultQuote), header(defaultHeader),
				encoding(defaultEncoding), rows(defaultRows)};
	}

}
