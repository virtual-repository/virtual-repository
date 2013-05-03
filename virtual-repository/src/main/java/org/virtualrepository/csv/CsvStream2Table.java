package org.virtualrepository.csv;

import java.io.InputStream;

import org.virtualrepository.spi.Transform;
import org.virtualrepository.tabular.Table;

/**
 * A {@link Transform} from {@link InputStream} to {@link Table} for {@link CsvAsset} assets.
 * 
 * @author Fabio Simeoni
 *
 */
public class CsvStream2Table implements Transform<CsvAsset,InputStream,Table> {
	
	@Override
	public Table apply(CsvAsset asset,InputStream input) {
		return new CsvTable(asset,input);
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
