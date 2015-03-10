package org.acme;

import static java.util.Arrays.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.virtualrepository.AssetType.*;
import static org.virtualrepository.VR.*;

import java.util.Optional;

import lombok.SneakyThrows;

import org.junit.Test;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.impl.Transforms;
import org.virtualrepository.spi.ReaderAdapter;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;
import org.virtualrepository.spi.WriterAdapter;

public class TransformTest {
	
	
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
		
		VirtualReader reader = readerFor(any,String.class);
		
		when(reader.retrieve(any(Asset.class))).thenReturn("2");
		
		//cannot use reader, mocks and default methods don't like each other.
		VirtualReader adapted = ReaderAdapter.adapt(reader,toNum);
		
		Asset a = testAsset().of(any).in(repo().get());

		assertSame(2,adapted.retrieve(a));
	}
	
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void transform_writers() {
		
		AssetType type = type();
		
		VirtualWriter writer = writerFor(type,String.class);
		
		//cannot use reader, mocks and default methods don't like each other.
		VirtualWriter adapted = WriterAdapter.adapt(writer,toString);
		
		Asset a = testAsset().of(any).in(repo().get());
		
		adapted.publish(a,2);
		
		verify(writer).publish(a, "2");
	}
	
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void derive_reader() {
		
		Transform<Integer,Boolean> toBoolean = transform(any).from(Integer.class).to(Boolean.class).with(n->n>0);
		
		Transforms transforms = transforms(toNum,toString,toBoolean);
		
		VirtualReader<String> reader = readerFor(any,String.class);
		
		when(reader.retrieve(any(Asset.class))).thenReturn("2");
		
		
		////////////////////////
		
		// string->integer->boolean
		
		Optional<VirtualReader<Boolean>> derived = transforms.inferReader(asList(reader),any,Boolean.class);
		
		assertTrue(derived.isPresent());
		
		Asset a = testAsset().of(any).in(repo().get());

		assertTrue(derived.get().retrieve(a));
		
		//bad case
		
		class SomeFancy {}
		
		assertFalse(transforms.inferReader(asList(reader),any,SomeFancy.class).isPresent());
		
	}
	
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void derive_writer() {
		
		Transform<Integer,Boolean> toBoolean = transform(any).from(Integer.class).to(Boolean.class).with(n->n>0);
		
		Transforms transforms = transforms(toNum,toString,toBoolean);
		
		VirtualWriter<Boolean> writer = writerFor(any,Boolean.class);
		
		////////////////////////
		
		// string->integer->boolean
		
		Optional<VirtualWriter<String>> derived = transforms.inferWriter(asList(writer),any,String.class);
		
		assertTrue(derived.isPresent());
		
		Asset a = testAsset().of(any).in(repo().get());

		derived.get().publish(a,"2");
		
		verify(writer).publish(a,true);
		
		//bad case
		
		class SomeFancy {}
		
		assertFalse(transforms.inferWriter(asList(writer),any,SomeFancy.class).isPresent());
		
	}
	
	
}
