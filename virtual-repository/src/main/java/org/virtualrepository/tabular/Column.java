package org.virtualrepository.tabular;

import static org.virtualrepository.Utils.*;

import javax.xml.namespace.QName;

/**
 * A column in a {@link Table}.
 * <p>
 * Columns have qualified names, qualified kinds (categorical/role-based description), and a data type.
 * 
 * @author Fabio Simeoni
 *
 */
public class Column {

	private final QName name;
	private QName kind;
	private Class<?> type;
	
	/**
	 * Adapts a {@link String} to a {@link QName}
	 * @param string
	 * @return the qualified name
	 */
	public static QName q(String string) {
		notNull(string);
		return new QName(string);
	}
	
	/**
	 * Creates an instance with a given name.
	 * @param name the name.
	 */
	public Column(QName name) {
		this(name,String.class);
	}
	
	/**
	 * Creates an instance with a given name.
	 * @param name the name.
	 */
	public Column(String name) {
		this(new QName(name),String.class);
	}
	
	/**
	 * Creates an instance with a given name and a given type.
	 * @param name the name 
	 * @param type the type
	 */
	public Column(QName name,Class<?> type) {
		valid(name);
		this.name=name;
		notNull("type",type);
		this.type=type;
	}
	
	
	/**
	 * Creates an instance with a given name and a given kind.
	 * @param name
	 * @param kind
	 */
	public Column(QName name,QName kind) {
		this(name);
		setKind(kind);
	}
	
	/**
	 * Creates an instance with a given name, kind, and type.
	 * @param name the name
	 * @param kind the kind
	 * @param type the type
	 */
	public Column(QName name,QName kind, Class<?> type) {
		this(name,kind);
		setType(type);
	}
	
	/**
	 * Returns the name of this column.
	 * @return the name
	 */
	public QName name() {
		return name;
	}
	
	/**
	 * Returns the kind of this column (<code>null</code> by default).
	 * @return the kind
	 */
	public QName getKind() {
		return kind;
	}
	
	/**
	 * Sets the kind of this column (<code>null</code> by default)
	 * @param kind the kind
	 */
	public void setKind(QName kind) {
		this.kind = kind;
	}
	
	/**
	 * Returns the type of this column ( {@link String} by default)
	 * @return the type
	 */
	public Class<?> type() {
		return type;
	}
	
	/**
	 * Sets the type of this column, overriding the default ({@link String} by default)
	 * @param type the type
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Column other = (Column) obj;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		
	}
}
