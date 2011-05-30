package net.glowstone.world;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

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
	public GlowChunk generate(GlowWorld world, int x, int z);

}
