package org.virtualrepository.tabular;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.virtualrepository.Properties;


@RequiredArgsConstructor
public class AbstractTable {

	@NonNull @Getter
	protected final List<Column> columns;
	
	@Getter
	private final Properties properties = new Properties();

	
	@Override
	public String toString() {
		final int maxLen = 100;
		return "Table [columns="
				+ (columns != null ? columns.subList(0, Math.min(columns.size(), maxLen)) : null) + ", properties="
				+ properties + "]";
	}

}