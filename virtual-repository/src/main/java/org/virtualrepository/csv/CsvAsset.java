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
import org.virtualrepository.spi.RepositoryService;
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
	 * Creates an instance with a given type, identifier, name, and {@link RepositoryService}.
	 * 
	 * @param type the type
	 * @param id the identifier
	 * @param name the name
	 * @param service the repository
	 * @param properties the properties
	 */
	protected <T extends CsvAsset> CsvAsset(Type<T> type, String id, String name, RepositoryService service) {
		super(type,id, name, service, defaultProperties());

	}
	
	/**
	 * Creates an instance with the generic {@link #type}, a given identifier, name, and {@link RepositoryService}.
	 * 
	 * @param id the identifier
	 * @param name the name
	 * @param service the repository
	 * @param properties the properties
	 */
	public <T extends CsvAsset> CsvAsset(String id, String name, RepositoryService service) {
		this(type,id, name, service);
	}

	/**
	 * Returns the delimiter character used in the content of this asset (by default a comma, <code>\u002C</code>).
	 * 
	 * @return the delimiter character
	 */
	public char delimiter() {
		return properties().lookup(delimiter, Character.class).value();
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
		return properties().lookup(quote, Character.class).value();
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
		return properties().lookup(header, Boolean.class).value();
	}

	/**
	 * Indicates whether the content of this asset has a header row, overriding the default (<code>false</code>).
	 * 
	 * @param header <code>true</code> if the content of this asset has a header row
	 */
	public void setHeader(boolean header) {
		properties().add(header(header));
	}

	/**
	 * Returns the encoding of the content of this asset (by default <code>UTF-8</code>).
	 * 
	 * @return the encoding of the content of this asset
	 */
	public Charset encoding() {
		return properties().lookup(encoding, Charset.class).value();
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

		return (List<Column>) properties().lookup(columns, List.class).value();

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

		return properties().lookup(rows, Long.class).value();

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

	private static Property<Character> delimiter(char d) {
		return new Property<Character>(delimiter, d, "column delimiter character");
	}

	private static Property<Character> quote(char q) {
		return new Property<Character>(quote, q, "value quote character");
	}

	private static Property<Boolean> header(boolean h) {
		return new Property<Boolean>(header, h, "flags existence of a header row");
	}

	private static Property<Charset> encoding(Charset c) {
		return new Property<Charset>(encoding, c, "charset for content encoding");
	}

	private static Property<List<Column>> columns(Column... cols) {
		return new Property<List<Column>>(columns, unmodifiableList(asList(cols)), "columns");
	}

	private static Property<Long> rows(long r) {
		return new Property<Long>(rows, r, "number of rows");
	}
	

	private static Property<?>[] defaultProperties() {

		return new Property[] { delimiter(defaultDelimiter), quote(defaultQuote), header(defaultHeader),
				encoding(defaultEncoding), rows(defaultRows) };
	}

}
