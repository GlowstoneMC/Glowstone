package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.World;

// TODO: Use this interface to reduce duplicate code in BlockPopulator subclasses.
// TODO: Refactor GenericTree to implement this class.

/**
 * A terrain feature that can randomly generate itself at specified locations, replacing blocks.
 */
@FunctionalInterface
public interface TerrainObject {
    /**
     * Generates this feature.
     *
     * @param world the world to generate in
     * @param random the PRNG that will choose the size and a few details of the shape
     * @param sourceX the base X coordinate
     * @param sourceY the base Y coordinate
     * @param sourceZ the base Z coordinate
     * @return true if successfully generated
     */
    boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ);
}
