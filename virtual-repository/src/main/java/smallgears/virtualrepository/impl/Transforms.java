package smallgears.virtualrepository.impl;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static smallgears.api.Apikit.*;
import static smallgears.virtualrepository.common.Utils.*;
import static smallgears.virtualrepository.spi.ReaderAdapter.*;
import static smallgears.virtualrepository.spi.WriterAdapter.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import smallgears.virtualrepository.Asset;
import smallgears.virtualrepository.AssetType;
import smallgears.virtualrepository.spi.Accessor;
import smallgears.virtualrepository.spi.ReaderAdapter;
import smallgears.virtualrepository.spi.Transform;
import smallgears.virtualrepository.spi.VirtualReader;
import smallgears.virtualrepository.spi.VirtualWriter;

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
	
	
	public Transforms add(@NonNull Transform<?,?> ... transforms) {
		
		return add(Arrays.asList(transforms));
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
	public <A extends Asset,T> Optional<VirtualReader<T>> inferReader(@NonNull List<VirtualReader<?>> base, @NonNull AssetType type, @NonNull Class<T> targetApi) {
		
		//transforms over compatible types
		List<Transform<?,?>> compatibleTransforms = transformsOver(type);
		
		//return first (derived) reader that fits the bill
		return (Optional) base.stream()
						 .filter(compatibleWith(type))
						 .map(compatibleReader->$derive(compatibleReader,compatibleTransforms,targetApi))
						 .flatMap(derived -> derived.isPresent() ? Stream.of(derived.get()) : empty()) //flatten (will remain awkward until java 9)
						 .findAny();			
		
	}
	
	
	/**
	 * Uses these transforms to infer a writer for a given type and API, starting from a base of one or more writers. 
	 */
	public <T> Optional<VirtualWriter<T>> inferWriter(@NonNull List<VirtualWriter<?>> base, @NonNull AssetType type, @NonNull Class<T> targetApi) {
		
		List<Transform<?,?>> CompatibleTransforms = transformsOver(type);
		
		//return first (derived) reader that fits the bill
		return (Optional) base.stream()
							 .filter(compatibleWith(type))
							 .map(compatibleWriter->$derive(compatibleWriter,CompatibleTransforms,targetApi))
							 .flatMap(derived -> derived.isPresent() ? Stream.of(derived.get()) : empty()) //flatten (awkward until java 9)
							 .findAny();			
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//starts recursion with no premises
	private Optional<VirtualReader> $derive(@NonNull VirtualReader reader, List<Transform<?,?>> transforms, Class targetApi) {
		return $derive(reader,transforms,targetApi,new ArrayList<>());
	}
	
	@SuppressWarnings("all")
	private Optional<VirtualReader> $derive(@NonNull VirtualReader reader, List<Transform<?,?>> compatibleTransforms, Class targetApi, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(reader.api()))
				return Optional.empty();
			
		   //do we have it already? (narrower api)
			if (ordered(reader.api(),targetApi))
				return Optional.of(reader);
		
			//remember to avoid future cycles 
			premises.add(reader.api());
			
			//can we move forward with some transform?
			Stream<Optional<VirtualReader>> s = compatibleTransforms.stream()
					  .filter(composeableWith(reader))
					  .map(composeableTransform->$derive(adapt(reader,composeableTransform),
																	  compatibleTransforms,
																	  targetApi,
																	  premises)); // derive new reader and recurse over that
			
			//we break this, otherwise we hit a javac bug (up to 1.8.0_20)
			return s.flatMap(o -> o.isPresent() ? Stream.of(o.get()) : empty()).findAny();	
		
	}

	private Optional<VirtualWriter> $derive(@NonNull VirtualWriter writer, List<Transform<?,?>> transforms, Class target) {
		return $derive(writer,transforms,target,new ArrayList<>());
	}
	
	private Optional<VirtualWriter> $derive(@NonNull VirtualWriter writer, @NonNull List<Transform<?,?>> compatibleTransforms, Class targetApi, List<Class> premises) {
		
			//short-circuit cycles
			if (premises.contains(writer.api()))
				return Optional.empty();
			
		   //do we have it already?
			if (ordered(writer.api(),targetApi))
				return Optional.of(writer);
		

			premises.add(writer.api());
			
			//can we move forward with some transform?
			Stream<Optional<VirtualWriter>> s =  compatibleTransforms.stream()
					  .filter(composeableWith(writer)) 
					  .map(composeableTransform->$derive(adapt(writer,composeableTransform),
							  							 compatibleTransforms,
							  							 targetApi,
							  							 premises));	  // derive new reader and recurse over that
					  
			//we break this, otherwise we hit a javac bug (up to 1.8.0_20)
			return s.flatMap((Optional<VirtualWriter> o) -> o.isPresent() ? Stream.of(o.get()) : empty()) //maps to non-null values (no better idiom until java 9)
					 .findAny();	
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private List<Transform<?,?>> transformsOver(AssetType type) {
		return transforms.stream().filter(t->ordered(type, t.type())).collect(toList());
	}
	
	
	private static Predicate<Accessor<?>> compatibleWith(AssetType type) {
		return accessor->ordered(type,accessor.type()); 
	}
	
	private static Predicate<Transform<?,?>> composeableWith(VirtualReader<?> reader) {
		return transform->ordered(reader.api(),transform.sourceApi()); 
	}
	
	private static Predicate<Transform<?,?>> composeableWith(VirtualWriter<?> writer) {
		return transform->ordered(transform.targetApi(),writer.api()); 
	}
	
	
}
