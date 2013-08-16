package org.acme;

import static org.acme.TestMocks.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.virtualrepository.csv.CsvAsset;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Column;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class IoTest {

	@Test
	public void discoveredCsvCanTravelTheWire() throws Exception {
	
		CsvAsset csv = new CsvAsset("testid","testname");
		CsvAsset parsed = xmlRoundTripOf(csv);
		
		System.out.println(csv);	
		System.out.println(parsed);	
		
		assertEquals(csv,parsed);
	}
	
	@Test
	public void publishedCsvCanTravelTheWire() throws Exception {
	
		CsvAsset csv = new CsvAsset("testid","testname",aService().name("repotest").get());
		CsvAsset parsed = xmlRoundTripOf(csv);
		
		System.out.println(csv);	
		System.out.println(parsed);	
		
		assertNotEquals(csv,parsed); //service proxies are dropped
	}
	
	@Test
	public void discoveredCsvCodelistsCanTravelTheWire() throws Exception {
	
		CsvCodelist codelist = new CsvCodelist("testid","testname",0);
		
		Column c = new Column(new QName("code"), new QName("code"), String.class);
		codelist.setColumns(c);
		
		CsvCodelist parsed = xmlRoundTripOf(codelist);
		
		System.out.println(codelist);	
		System.out.println(parsed);	
		
		assertEquals(codelist,parsed);
	}
	
	@Test
	public void publishedCsvCodelistsCanTravelTheWire() throws Exception {
	
		CsvCodelist codelist = new CsvCodelist("testid","testname",0,aService().name("repotest").get());
		
		Column c = new Column(new QName("code"), new QName("code"), String.class);
		codelist.setColumns(c);
		
		CsvCodelist parsed = xmlRoundTripOf(codelist);
		
		System.out.println(codelist);	
		System.out.println(parsed);	
		
		assertNotEquals(codelist,parsed); //service proxies are dropped
		
	}
	
	
	@Test
	public void discoveredSdmxCodelistCanTravelTheWire() throws Exception {
	
		SdmxCodelist codelist = new SdmxCodelist("urn","id", "1.0", "name");
		SdmxCodelist parsed = xmlRoundTripOf(codelist);
		
		System.out.println(codelist);	
		System.out.println(parsed);
		
		assertEquals(codelist,parsed);
	}
	
	@Test
	public void publishedSdmxCodelistCanTravelTheWire() throws Exception {
	
		SdmxCodelist codelist = new SdmxCodelist("name",aService().name("repotest").get());
		SdmxCodelist parsed = xmlRoundTripOf(codelist);
		
		System.out.println(codelist);	
		System.out.println(parsed);
		
		assertNotEquals(codelist,parsed);
		
	}
	
	static <T> T xmlRoundTripOf(T o) throws Exception {

		XStream x = new XStream(new StaxDriver());
		
		StringWriter w = new StringWriter();
		x.marshal(o, new PrettyPrintWriter(w));
		
		System.out.println(w.toString());
		
		@SuppressWarnings("all")
		T read = (T) x.fromXML(w.toString());

		return read;
	}
	
}
