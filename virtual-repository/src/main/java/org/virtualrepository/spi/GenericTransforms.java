package org.virtualrepository.spi;

import static org.virtualrepository.AssetType.*;
import static org.virtualrepository.VR.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;

import org.virtualrepository.Asset;

@UtilityClass
public class GenericTransforms {

	/**
	 * Transforms strings to input stream.
	 */
	public static <A extends Asset> Transform<A,String,InputStream> string2streamFor(Class<A> assets) {
		
		return transform(assets)
				.type(any)
				.from(String.class)
				.to(InputStream.class)
				.with(s->new ByteArrayInputStream(s.getBytes()));
	}

	/**
	 * Transform streams to strings.
	 */
	public static <A extends Asset> Transform<A,InputStream,String> stream2stringFor(Class<A> assets) {
		
		return transform(assets).type(any).from(InputStream.class).to(String.class).with(s->{
		
					@Cleanup Scanner sc = new Scanner(s);
					sc.useDelimiter("\\A");
				    return sc.hasNext() ? sc.next() : "";
				
		});
	}
}
