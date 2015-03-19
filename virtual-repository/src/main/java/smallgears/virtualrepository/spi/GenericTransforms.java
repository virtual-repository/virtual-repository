package smallgears.virtualrepository.spi;

import static smallgears.virtualrepository.AssetType.*;
import static smallgears.virtualrepository.VR.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GenericTransforms {

	/**
	 * Transforms strings to input stream.
	 */
	public static Transform<String,InputStream> string2stream = 
			transform(any).from(String.class).to(InputStream.class).with(s->new ByteArrayInputStream(s.getBytes()));

	/**
	 * Transform streams to strings.
	 */
	public static Transform<InputStream,String> stream2string = 
			
			transform(any).from(InputStream.class).to(String.class).with(s->{
		
					@Cleanup Scanner sc = new Scanner(s);
					sc.useDelimiter("\\A");
				    return sc.hasNext() ? sc.next() : "";
				
			});
}
