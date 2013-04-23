package org.fao.virtualrepository;

import java.util.Iterator;

import org.fao.virtualrepository.impl.Repositories;
import org.fao.virtualrepository.spi.Repository;

/**
 * A repository virtually comprised of data {@link Asset}s held in a number of underlying {@link Repository}s, the
 * so-called <em>concrete repositories.</em>
 * 
 * <p>
 * 
 * Concrete repositories may, and usually will, be remotely accessible. Some of the operations of a virtual repository
 * may thus trigger network interactions, as clearly specified in their documentation.
 * 
 * <p>
 * 
 * A virtually repository can be populated with {@link Asset}s of one ore more {@link AssetType}s (cf. {@link #ingest(AssetType...)}.. 
 * It can then return the ingested {@link Asset}s under the {@link Iterable} interface, or look them up by identifier (cf. {@link #get(String)}.  
 * 
 * @author Fabio Simeoni
 * 
 */
public interface VirtualRepository extends Iterable<Asset> {

	/**
	 * Returns the concrete repositories underlying this repository.
	 * 
	 * @return
	 */
	Repositories repositories();

	/**
	 * Ingests in this repository all the assets of given {@link AssetType}s which are held in the concrete
	 * {@link Repository}s.
	 * <p>
	 * This method implements a discovery operation and <em>may</em> involve networked interactions with remote
	 * repositories. However, it does <em>not</em> imply the transfer of actual data streams. Rather, it <em>may</em>
	 * imply the transfer of summary information about the assets.
	 * <p>
	 * This method may be invoked multiple times to incrementally ingest assets of different types. Ingesting an asset
	 * overwrites any asset with the same identifier which may have been previously ingested.
	 * 
	 * @param types the asset types
	 */
	void ingest(AssetType<?>... types);

	/**
	 * Returns all the {@link Asset}s ingested so far in this repository.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 * @return the assets
	 */
	Iterator<Asset> iterator();
	
		
	/**
	 * Returns an asset previously ingested in this repository.
	 * <p>
	 * This is a local operation and does not trigger network interactions.
	 * 
	 * @param id the asset identifier
	 * @return the asset
	 * 
	 * @throws IllegalStateException if an asset with the given identifier was not ingested in this repository
	 */
	Asset get(String id);
}
