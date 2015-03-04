package org.virtualrepository.impl;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static org.virtualrepository.common.Utils.*;
import static smallgears.api.Apikit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.NonNull;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.ReaderAdapter;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;
import org.virtualrepository.spi.WriterAdapter;


@SuppressWarnings("all") //this is all reflection-based, get the type checker out of the way

public class Transforms {

	@NonNull
	Map<AssetType,List<Transform<?,?,?>>> transforms;
	
	public Transforms(Iterable<Transform<?,?,?>> transforms) {
		this.transforms = streamof(transforms).collect(groupingBy(Transform::type));
	}

	/**
	 * Uses these transforms to derive a reader from an initial a base of one or more readers. 
	 */
	public <A extends Asset,T> Optional<VirtualReader<A,T>> inferReader(@NonNull List<VirtualReader<?,?>> base, @NonNull AssetType type, @NonNull Class<T> target) {
		
		List<Transform<?,?,?>> transforms = this.transforms.get(type);
		
		//return first reader that has a derivation
		return (Optional) base.stream()
							 .map(r->$derive(r,transforms,target,new ArrayList<>()))
							 .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty())
							 .findAny();			
		
	}
	
	
	/**
	 * Uses these transforms to derive a writer from an initial base of one or more writers, if possible. 
	 */
	public <A extends Asset,T> Optional<VirtualWriter<A,T>> inferWriter(@NonNull List<VirtualWriter<?,?>> base, @NonNull AssetType type, @NonNull Class<T> target) {
		
		List<Transform<?,?,?>> transforms = this.transforms.get(type);
		
		//return first reader that has a derivation
		return (Optional) base.stream()
							 .map(r->$derive(r,transforms,target,new ArrayList<>()))
							 .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty())
							 .findAny();			
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@SuppressWarnings("all")
	private Optional<VirtualReader> $derive(@NonNull VirtualReader reader, @NonNull List<Transform<?,?,?>> transforms, Class target, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(reader.api()))
				return Optional.empty();
			
		   //do we have it already?
			if (ordered(reader.api(),target))
				return Optional.of(reader);
		

			premises.add(reader.api());
			
			//can we move forward with some transform?
			return transforms.stream()
					  .filter(t->ordered(reader.api(),t.sourceApi()))         // can be extended with it
					  .map(t->$derive(ReaderAdapter.adapt(reader,t),transforms,target,premises))	  // derive new reader and recurse over that
					  .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty()) //maps to non-null values (no better idiom until java 9)
					  .findAny();	
		
	}
	
	private Optional<VirtualWriter> $derive(@NonNull VirtualWriter writer, @NonNull List<Transform<?,?,?>> transforms, Class target, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(writer.api()))
				return Optional.empty();
			
		   //do we have it already?
			if (ordered(writer.api(),target))
				return Optional.of(writer);
		

			premises.add(writer.api());
			
			//can we move forward with some transform?
			return transforms.stream()
					  .filter(t->ordered(t.targetApi(),writer.api()))         // can be extended with it
					  .map(t->$derive(WriterAdapter.adapt(writer,t),transforms,target,premises))	  // derive new reader and recurse over that
					  .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty()) //maps to non-null values (no better idiom until java 9)
					  .findAny();	
		
	}
	
}
