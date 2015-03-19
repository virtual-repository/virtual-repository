package org.acme.samples;

import static org.acme.Mocks.*;

import java.util.Arrays;

import lombok.Getter;
import smallgears.virtualrepository.spi.Transform;
import smallgears.virtualrepository.spi.VirtualExtension;

public class TestExtension implements VirtualExtension {
	
	@Getter
	String name="test-extension";

	@Override
	public Iterable<Transform<?, ?>> transforms() {
		return Arrays.asList(toNum,toString);
	}
}
