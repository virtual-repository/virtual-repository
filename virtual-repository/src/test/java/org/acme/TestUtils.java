package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

public class TestUtils {

	
	public static void assertEqualElements(Iterable<?> it1, Iterable<?> it2) {
		Assert.assertEquals(asList(it1),asList(it2));
	}
	
	public static <T> List<T> asList(Iterable<T> it) {
		List<T> set = new ArrayList<T>();
		for (T t : it)
			set.add(t);
		return set;
	}
	

}
