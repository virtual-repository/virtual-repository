package org.virtualrepository.impl;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static org.virtualrepository.common.Utils.*;
import static smallgears.api.Apikit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.spi.ReaderAdapter;
import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualReader;
import org.virtualrepository.spi.VirtualWriter;
import org.virtualrepository.spi.WriterAdapter;

import smallgears.api.Apikit;

/**
 * Pools API transformations for asset content and uses them to infer readers and writers.
 * <p>
 * Optionally (but typically), can load transforms from the classpath, 
 */
@SuppressWarnings("all") //this is all reflection-based, get the type checker out of the way
@Slf4j(topic="virtual-repository")
public class Transforms implements Iterable<Transform<?,?>> {

	List<Transform<?,?>> transforms = new ArrayList<>();
	
	public Transforms(@NonNull Iterable<Transform<?,?>> transforms) {
		add(transforms);
	}
	
	public Transforms(@NonNull Transform<?,?> ... transforms) {
		this(Arrays.asList(transforms));
	}
	
	@Override
	public Iterator<Transform<?, ?>> iterator() {
		return transforms.iterator();
	}
	
	
	public Transforms add(@NonNull Iterable<Transform<?,?>> transforms) {
		
		List<Transform<?,?>> collected = streamof(transforms).collect(toList());
		
		this.transforms.addAll(collected);
		
		if (!collected.isEmpty()) log.info("added transform(s): {}",collected);
		
		return this;
	}

	/**
	 * Uses these transforms to infer a reader for a given type and API, starting from a base of one or more readers. 
	 */
	public <A extends Asset,T> Optional<VirtualReader<T>> inferReader(@NonNull List<VirtualReader<?>> base, @NonNull AssetType type, @NonNull Class<T> target) {
		
		List<Transform<?,?>> matching = matching(type);
		
		//return first (derived) reader that fits the bill
		return (Optional) base.stream()
							 .map(reader->$derive(reader,matching,target))
							 .flatMap(result -> result.isPresent() ? Stream.of(result.get()) : empty()) //flatten (awkward until java 9)
							 .findAny();			
		
	}
	
	
	/**
	 * Uses these transforms to infer a writer for a given type and API, starting from a base of one or more writers. 
	 */
	public <T> Optional<VirtualWriter<T>> inferWriter(@NonNull List<VirtualWriter<?>> base, @NonNull AssetType type, @NonNull Class<T> api) {
		
		List<Transform<?,?>> matching = matching(type);
		
		//return first (derived) reader that fits the bill
		return (Optional) base.stream()
							 .map(writer->$derive(writer,matching,api))
							 .flatMap(result -> result.isPresent() ? Stream.of(result.get()) : empty()) //flatten (awkward until java 9)
							 .findAny();			
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private List<Transform<?,?>> matching(AssetType type) {
		return transforms.stream().filter(t->ordered(type, t.type())).collect(toList());
	}
	
	private Optional<VirtualReader> $derive(@NonNull VirtualReader reader, List<Transform<?,?>> transforms, Class target) {
		return $derive(reader,transforms,target,new ArrayList<>());
	}
	
	@SuppressWarnings("all")
	private Optional<VirtualReader> $derive(@NonNull VirtualReader reader, List<Transform<?,?>> transforms, Class target, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(reader.api()))
				return Optional.empty();
			
		   //do we have it already?
			if (ordered(reader.api(),target))
				return Optional.of(reader);
		

			premises.add(reader.api());
			
			//can we move forward with some transform?
			Stream<Optional<VirtualReader>> s = transforms.stream()
					  .filter(t->ordered(reader.api(),t.sourceApi()))         // can be extended with it
					  .map(t->$derive(ReaderAdapter.adapt(reader,t),transforms,target,premises));	  // derive new reader and recurse over that
			
			//we break this, otherwise we hit a javac bug (up to 1.8.0_20)
			return s.flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty()).findAny();	
		
	}

	private Optional<VirtualWriter> $derive(@NonNull VirtualWriter writer, List<Transform<?,?>> transforms, Class target) {
		return $derive(writer,transforms,target,new ArrayList<>());
	}
	
	private Optional<VirtualWriter> $derive(@NonNull VirtualWriter writer, @NonNull List<Transform<?,?>> transforms, Class target, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(writer.api()))
				return Optional.empty();
			
		   //do we have it already?
			if (ordered(writer.api(),target))
				return Optional.of(writer);
		

			premises.add(writer.api());
			
			//can we move forward with some transform?
			Stream<Optional<VirtualWriter>> s =  transforms.stream()
					  .filter(t->ordered(t.targetApi(),writer.api()))         // can be extended with it
					  .map(t->$derive(WriterAdapter.adapt(writer,t),transforms,target,premises));	  // derive new reader and recurse over that
					  
			//we break this, otherwise we hit a javac bug (up to 1.8.0_20)
			return s.flatMap((Optional<VirtualWriter> o) -> o.isPresent() ? Stream.of(o.get()) : empty()) //maps to non-null values (no better idiom until java 9)
					 .findAny();	
		
	}
}
