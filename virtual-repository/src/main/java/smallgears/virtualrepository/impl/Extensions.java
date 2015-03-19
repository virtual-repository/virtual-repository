package smallgears.virtualrepository.impl;

import static java.lang.String.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static smallgears.api.Apikit.*;

import java.util.List;
import java.util.ServiceLoader;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import smallgears.api.group.Group;
import smallgears.virtualrepository.spi.VirtualExtension;

@Slf4j
public class Extensions extends Group<VirtualExtension,Extensions> {
	
	@Getter
	Transforms transforms = new Transforms();
	
	public Extensions(@NonNull VirtualExtension ... extensions) {
		
		super(VirtualExtension::name);
		
		add(extensions);
	}
	
	/**
	 * Loads extensions from the classpath.
	 */
	public Extensions load() {

		ServiceLoader<VirtualExtension> loaded = ServiceLoader.load(VirtualExtension.class);

		List<VirtualExtension> extensions =  streamof(loaded).collect(toList());
		
		for (VirtualExtension extension : extensions)
			
			try {
				
				load(extension);
				
			}
			catch(Throwable e) {
				log.error(format("extension %s cannot be activated and will be discarded (see cause)",extension.getClass()),e);
				continue;
			}

		log.info(extensions.isEmpty() ? 
				"no extension found on classpath!":
				"loaded {} extension(s)",size());
		
		return this;
	}
	
	
	
	/**
	 * Tell all repositories and plugins to shutdown (if they want to know about it).
	 */
	public void shutdown() {
		
		forEach(extension-> {
			
			remove(extension);

			try {
				extension.shutdown();
			}
			catch(Throwable t) {
				log.warn("no clean shutdown for extension "+extension.name()+" (see cause)",t);
			}
			
			
		});
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	protected void add(VirtualExtension extension) {
		
		validate(extension);
		
		
		transforms.add(extension.transforms());
		
		super.add(extension);
		
		log.info("added extension: {}",extension.name());
	}
	
	private void load(VirtualExtension extension) throws Exception {
		
		extension.init();
		
		add(extension);
	}
	
	
	
	private void validate(VirtualExtension extension) throws IllegalArgumentException {
		
		try {
			
			requireNonNull(extension.name(),"extension name");
			
			if (has(extension.name()))
				log.warn("{} supersides extension registered previously with the same name");
			
		}
		catch(Exception e) {
			throw new IllegalArgumentException("invalid extension "+extension.name()+" (see cause)",e);
		}
	}

}
