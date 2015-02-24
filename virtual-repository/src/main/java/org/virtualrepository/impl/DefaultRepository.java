package org.virtualrepository.impl;

import static java.util.stream.Collectors.*;

import java.util.Set;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.virtualrepository.AssetType;
import org.virtualrepository.Repository;
import org.virtualrepository.spi.Accessor;
import org.virtualrepository.spi.VirtualProxy;

import smallgears.api.properties.Properties;

@RequiredArgsConstructor
@ToString(of={"name","properties"})
public final class DefaultRepository implements Repository {

	@NonNull @Getter
	private final String name;
	
	@NonNull @Getter
	private final VirtualProxy proxy;
	
	@Getter
	private final Properties properties = Properties.props();
	
	@Override
	public boolean takes(@NonNull AssetType ... types) {
		return Stream.of(types).allMatch(proxy.publishers()::contains);
	}
	
	@Override
	public Set<AssetType> typesTaken() {
		return proxy.publishers().stream().map(Accessor::type).collect(toSet());
	}
	
	@Override
	public boolean returns(@NonNull AssetType ... types) {
		return Stream.of(types).allMatch(proxy.importers()::contains);
	}
	
	@Override
	public Set<AssetType> typesReturned() {
		return proxy.importers().stream().map(Accessor::type).collect(toSet());
	}
	
}
