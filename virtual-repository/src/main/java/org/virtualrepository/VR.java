package org.virtualrepository;

import org.virtualrepository.impl.DefaultVirtualRepository;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VR {

	/**
	 * A group of repositories.
	 */
	public static Repositories repositories(Repository ... repositories) {
		return new Repositories(repositories);
	}
	
	/**
	 * A virtual repository over all the base repositories discovered on the classpath.
	 */
	public static VirtualRepository repository() {
		
		return new DefaultVirtualRepository(repositories().load());
	}
	
	/**
	 * A virtual repository over a given set of base repositories.
	 */
	public static VirtualRepository repository(Repository ... repositories) {
		
		return new DefaultVirtualRepository(repositories(repositories));
	}
}
