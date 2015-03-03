package org.acme;

import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.VR.*;
import lombok.SneakyThrows;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.spi.ReaderAdapter;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualReader;

public class TransformTest {
	
	
	Transform<Asset,String,Integer> toNum = 
			transform(Asset.class).from(String.class).to(Integer.class).with(Integer::valueOf);
	
	Transform<Asset,Integer,String> toString = 
			transform(Asset.class).from(Integer.class).to(String.class).with(String::valueOf);
	
	
	@Test @SneakyThrows
	public void transforms_errr_transform() {
		
		assertSame(2,toNum.apply(null,"2"));
	}
	
	@Test @SneakyThrows
	public void transforms_compose() {
		
		assertEquals("2",toNum.then(toString).apply(null,"2"));
		assertSame(2,toNum.after(toString).apply(null,2));
	}
	
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void transform_readers() {
		
		AssetType type = type();
		
		VirtualReader<Asset,String> reader = readerFor(type,String.class);
		
		when(reader.retrieve(any(Asset.class))).thenReturn("2");
		
		//cannot use reader, mocks and default methods don't like each other.
		VirtualReader<Asset,Integer> adapted = ReaderAdapter.adapt(reader,toNum);
		
		assertSame(2,adapted
				      .retrieve(asset()
				    		  .of(type)
				    		    .in(repo().get())));
	}
}
