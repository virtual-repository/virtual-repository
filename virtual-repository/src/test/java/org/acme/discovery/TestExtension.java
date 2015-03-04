package org.acme.discovery;

import static org.acme.Mocks.*;

import java.util.Arrays;

import lombok.Getter;

import org.virtualrepository.spi.Transform;
import org.virtualrepository.spi.VirtualExtension;

public class TestExtension implements VirtualExtension {
	
	@Getter
	String name="test-extension";

	@Override
	public Iterable<Transform<?, ?, ?>> transforms() {
		return Arrays.asList(toNum,toString);
	}
}
