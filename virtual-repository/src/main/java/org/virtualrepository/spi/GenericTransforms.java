package org.virtualrepository.spi;

import static org.virtualrepository.AssetType.*;
import static org.virtualrepository.VR.*;

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
	public Transform<String,InputStream> string2stream = 
			transform(any).from(String.class).to(InputStream.class).with(s->new ByteArrayInputStream(s.getBytes()));

	/**
	 * Transform streams to strings.
	 */
	public Transform<InputStream,String> stream2string = 
			
			transform(any).from(InputStream.class).to(String.class).with(s->{
		
					@Cleanup Scanner sc = new Scanner(s);
					sc.useDelimiter("\\A");
				    return sc.hasNext() ? sc.next() : "";
				
			});
}
