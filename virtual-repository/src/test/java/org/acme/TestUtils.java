package org.acme;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

public class TestUtils {

	
	public static void assertEqualElements(Iterable<?> it1, Iterable<?> it2) {
		assertEquals(toCollection(it1),toCollection(it2));
	}
	
	public static <T> Collection<T> toCollection(Iterable<T> it) {
		HashSet<T> set = new HashSet<T>();
		for (T t : it)
			set.add(t);
		return set;
	}
}
