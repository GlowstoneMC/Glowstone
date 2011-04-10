package net.glowstone.world;

import net.glowstone.Chunk;

/**
 * A {@link WorldGenerator} is used to populate new chunks which have just been
 * created.
 * @author Graham Edgecombe
 */
public interface WorldGenerator {

	/**
	 * Generates a new chunk.
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @return The chunk.
	 */
	public Chunk generate(int x, int z);

}
