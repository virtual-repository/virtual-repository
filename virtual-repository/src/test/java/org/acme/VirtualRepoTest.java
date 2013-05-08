package org.acme;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.acme.TestMocks.*;
import static org.acme.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.impl.Type;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.RepositoryService;
import org.virtualrepository.spi.ServiceProxy;

@SuppressWarnings({"unchecked","rawtypes"})
public class VirtualRepoTest {
	
	Type<Asset> type1 = aType();
	Type<Asset> type2 = aType();
	
	@BeforeClass
	public static void setup() {
		
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","trace");
	}
	
	@Test
	public void assetsCanBeDiscovered() throws Exception {
		
		ServiceProxy proxy1 = aProxy().with(anImporterFor(type1)).get();
		ServiceProxy proxy2 = aProxy().with(anImporterFor(type1)).get();
		
		
		RepositoryService service1 = aService().with(proxy1).get();
		RepositoryService service2 = aService().with(proxy2).get();
		
		Asset a1 = anAsset().of(type1).in(service1);
		Asset a2 = anAsset().of(type1).in(service2);
		Asset a3 = anAsset().of(type2).in(service2);
		
		when(proxy1.browser().discover(asList(type1))).thenReturn((Iterable)singleton(a1));
		when(proxy2.browser().discover(asList(type1))).thenReturn((Iterable)singleton(a2));
		
		
		//test
		
		VirtualRepository repo = new Repository(service1,service2);
		
		int discovered = repo.discover(type1);
		
		assertEquals(2,discovered);
		
		Asset retrieved = repo.lookup(a1.id());
		
		assertEquals(a1,retrieved);
		
		retrieved = repo.lookup(a2.id());
		
		assertEquals(a2,retrieved);
		
		try {
			repo.lookup(a3.id());
			fail();
		}
		catch(IllegalStateException e) {}
		
		assertEquals(2, asList(repo).size());
	}
	
	@Test
	public void assetsCanBeDiscoveredIncrementally() throws Exception {
		
		ServiceProxy proxy = aProxy().with(anImporterFor(type1)).get();
		RepositoryService service1 = aService().with(proxy).get();
		
		Asset a1 = anAsset().of(type1).in(service1);
		Asset a2 = anAsset().of(type1).in(service1);
		
		when(proxy.browser().discover(asList(type1))).thenReturn((Iterable)singleton(a1),(Iterable)asList(a1,a2));
		
		//test
		
		VirtualRepository repo = new Repository(service1);
		
		int size = repo.discover(type1);
		
		assertEquals(1,size);
		
		size = repo.discover(type1);
		
		assertEquals(1,size);
	}
	
	@Test
	public void discoveryFailuresAreTolerated() throws Exception {
		
		ServiceProxy proxy = aProxy().with(anImporterFor(type1)).get();
		RepositoryService service = aService().with(proxy).get();
		RepositoryService failing = aService().get();
		
		Asset a = anAsset().of(type1).in(service);
		
		when(proxy.browser().discover(asList(type1))).thenReturn((Iterable) singleton(a));
		when(failing.proxy().browser().discover(anyList())).thenThrow(new Exception());
		
		//test
		
		VirtualRepository repo = new Repository(service,failing);
		
		int discovered = repo.discover(type1);
		
		assertEquals(1,discovered);
	}
	
	
	@Test
	public void retrievalFailsWithoutReader() {
		
		RepositoryService service = aService().get();
		
		Asset asset = anAsset().in(service);
		
		VirtualRepository virtual = new Repository(service);
		
		//no reader for integers
		try {
			virtual.retrieve(asset,Integer.class);
			fail();
		}
		catch(IllegalStateException e) {}
		
	}
	
	@Test
	public void assetsAreRetrieved() throws Exception {
		
		final int data = 10;
		
		Importer<Asset,Integer> importer = anImporterFor(type1,Integer.class);
		
		ServiceProxy proxy = aProxy().with(importer).get();
		RepositoryService service = aService().with(proxy).get();
		
		Asset asset = anAsset().of(type1).in(service);
		
		when(importer.retrieve(asset)).thenReturn(data);
		
		//test 
		
		VirtualRepository virtual = new Repository(service);
		
		int imported = virtual.retrieve(asset,Integer.class);
		
		assertEquals(data, imported);
	}
	
	
	@Test
	public void assetsCanBePublished() throws Exception {
		
		Type<Asset> type = aType();
		Publisher<Asset,String> publisher = aPublisherFor(type,String.class);
		
		ServiceProxy proxy = aProxy().with(publisher).get();
		RepositoryService service = aService().with(proxy).get();
		
		Asset asset = anAsset().of(type).in(service);
		
		VirtualRepository virtual = new Repository(service);
		
		virtual.publish(asset,"hello");
		
		verify(publisher).publish(asset,"hello");
		
	}

}
