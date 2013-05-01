package org.fao.virtualrepository.csv;

import java.io.InputStream;

import org.fao.virtualrepository.spi.Transform;
import org.fao.virtualrepository.tabular.Table;

/**
 * A {@link Transform} from {@link InputStream} to {@link Table} for {@link CSV} assets.
 * 
 * @author Fabio Simeoni
 *
 */
public class CSVStream2Table implements Transform<CSV,InputStream,Table> {
	
	@Override
	public Table apply(CSV asset,InputStream input) {
		return new CSVTable(asset,input);
	}
	
	@Override
	public Class<InputStream> inputAPI() {
		return InputStream.class;
	}
	
	@Override
	public Class<Table> outputAPI() {
		return Table.class;
	}
}
