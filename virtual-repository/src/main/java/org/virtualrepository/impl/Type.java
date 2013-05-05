package org.virtualrepository.impl;

import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;

/**
 * Implementation-facing extension of {@link AssetType}.
 * 
 * <p>
 * Tightens the type contract of {@link AssetType} so as to promote safety of API implementations.
 *  
 * @author Fabio Simeoni
 *
 * @param <T> the language type of {@link Asset}s of this type
 */
public interface Type<T extends Asset> extends AssetType {

}
