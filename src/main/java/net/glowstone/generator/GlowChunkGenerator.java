package net.glowstone.generator;

import java.util.Random;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;

import net.glowstone.GlowChunk;

/**
 * A helper class for use with ChunkGenerator.
 * @author Tad
 */
public abstract class GlowChunkGenerator extends ChunkGenerator {
    
    protected byte[] data = new byte[GlowChunk.HEIGHT * GlowChunk.WIDTH * GlowChunk.DEPTH];
    
    /**
     * Clear data to a byte[] of the proper size.
     */
    protected void clear() {
        for (int i = 0; i < data.length; ++i) data[i] = 0;
    }
    
    /**
     * Set the given block to the given type.
     * @param x The chunk X coordinate.
     * @param y The Y coordinate.
     * @param z The chunk Z coordinate.
     * @param type The block type.
     */
    protected void set(int x, int y, int z, byte id) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) return;
        data[(x * GlowChunk.HEIGHT + z) * GlowChunk.DEPTH + y] = id;
    }

    /**
     * Shapes the chunk for the given coordinates.
     *
     * Note that this method should <b>never</b> attempt to get the Chunk at
     * the passed coordinates, as doing so may cause an infinite loop
     *
     * @param world The world this chunk will be used for
     * @param random The random generator to use
     * @param chunkX The X-coordinate of the chunk
     * @param chunkZ The Z-coordinate of the chunk
     * @return byte[32768] containing the types for each block created by this generator
     */
    public abstract byte[] generate(World world, Random random, int chunkX, int chunkZ);
    
    /**
     * Tests if the specified location is valid for a natural spawn position
     *
     * @param world The world we're testing on
     * @param x X-coordinate of the block to test
     * @param z Z-coordinate of the block to test
     * @return true if the location is valid, otherwise false
     */
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

}
