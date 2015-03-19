package smallgears.virtualrepository.common;

import java.time.Duration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

	public static final Duration default_discovery_timeout = Duration.ofMinutes(3);
	public static final Duration default_retrieval_timeout = Duration.ofMinutes(1);
	public static final Duration default_publish_timeout = Duration.ofMinutes(1);
	
	
}
