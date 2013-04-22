package org.acme;

import static org.junit.Assert.*;

import java.util.HashSet;

public class TestUtils {

	
	public static void assertEqualElements(Iterable<?> it1, Iterable<?> it2) {
		HashSet<Object> set1 = new HashSet<Object>();
		for (Object t : it1)
			set1.add(t);
		HashSet<Object> set2 = new HashSet<Object>();
		for (Object t : it2)
			set2.add(t);
		assertEquals(set1,set2);
	}
}
