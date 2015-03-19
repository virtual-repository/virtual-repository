package org.acme;

import static java.util.Arrays.*;
import static org.acme.Mocks.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static smallgears.virtualrepository.AssetType.*;
import static smallgears.virtualrepository.VR.*;

import java.util.Optional;

import lombok.SneakyThrows;

import org.junit.Test;

import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;
import smallgears.virtualrepository.impl.Transforms;
import smallgears.virtualrepository.spi.ReaderAdapter;
import smallgears.virtualrepository.spi.Transform;
import smallgears.virtualrepository.spi.VirtualReader;
import smallgears.virtualrepository.spi.VirtualWriter;
import smallgears.virtualrepository.spi.WriterAdapter;

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
	public void derive_reader_for_subtype() {
		
		AssetType subtype = type().specialises(some_type);
		
		VirtualReader<String> readsStrings = readerFor(some_type,String.class);
		
		when(readsStrings.retrieve(any(Asset.class))).thenReturn("2");
		
		//no transforms, focus on subtyping
		Transforms transforms = transforms();
		
		Optional<VirtualReader<String>> subtypeReader = transforms.inferReader(asList(readsStrings),subtype,String.class);
		
		assertTrue(subtypeReader.isPresent());
		
		assertEquals("2",subtypeReader.get().retrieve(someAsset()));
		
	}
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void cannot_derive_reader_for_supertype() {
		
		//no transforms, focus on subtyping
		Transforms transforms = transforms();

		VirtualReader<String> readsStrings = readerFor(some_type,String.class);
		
		Optional<VirtualReader<String>> anyReader = transforms.inferReader(asList(readsStrings),any,String.class);
		
		assertFalse(anyReader.isPresent());
	}
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void derive_reader_via_transform() {
		
		Transform<Integer,Boolean> int2bool = transform(some_type).from(Integer.class).to(Boolean.class).with(n->n>0);
		
		Transforms transforms = transforms(int2bool);
		
		VirtualReader<Integer> readsInt = readerFor(some_type,Integer.class);
		
		when(readsInt.retrieve(any(Asset.class))).thenReturn(2);
		
		////////////////////////
		
		// string->integer->boolean
		
		Optional<VirtualReader<Boolean>> derived = transforms.inferReader(asList(readsInt),some_type,Boolean.class);
		
		assertTrue(derived.isPresent());
		
		assertTrue(derived.get().retrieve(someAsset()));
		
		//bad case
		
		class SomeFancy {}
		
		assertFalse(transforms.inferReader(asList(readsInt),any,SomeFancy.class).isPresent());
		
	}
	
	@Test @SneakyThrows @SuppressWarnings("all")
	public void derive_reader_via_transform_and_subtyping() {
		
		Transform<Integer,Boolean> toBoolean = transform(some_type).from(Integer.class).to(Boolean.class).with(n->n>0);
		
		Transforms transforms = transforms(toNum,toString,toBoolean);
		
		VirtualReader<String> readsStrings = readerFor(some_type,String.class);
		
		when(readsStrings.retrieve(any(Asset.class))).thenReturn("2");
		
		////////////////////////
		
		AssetType subtype = type().specialises(some_type);
		
		// string->integer->boolean
		
		Optional<VirtualReader<Boolean>> derived = transforms.inferReader(asList(readsStrings),subtype,Boolean.class);
		
		assertTrue(derived.isPresent());
		
		assertTrue(derived.get().retrieve(someAsset()));
		
		//unrelated types don't work
		
		derived = transforms.inferReader(asList(readsStrings),some_other_type,Boolean.class);
		
		assertFalse(derived.isPresent());
		
		
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
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//assets are not used in these tests, any will do
	private Asset someAsset() {
		return assetOfSomeType().in(repo().get());
	}
}
