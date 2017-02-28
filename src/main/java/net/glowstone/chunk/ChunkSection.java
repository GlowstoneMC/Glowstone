package net.glowstone.chunk;

import net.glowstone.util.NibbleArray;

/**
 * A single cubic section of a chunk, with all data.
 */
public final class ChunkSection {
    private static final int ARRAY_SIZE = GlowChunk.WIDTH * GlowChunk.HEIGHT * GlowChunk.SEC_DEPTH;

    // these probably should be made non-public
    public final char[] types;
    public final NibbleArray skyLight;
    public final NibbleArray blockLight;
    public int count; // amount of non-air blocks

    /**
     * Create a new, empty ChunkSection.
     */
    public ChunkSection() {
        types = new char[ARRAY_SIZE];
        skyLight = new NibbleArray(ARRAY_SIZE);
        blockLight = new NibbleArray(ARRAY_SIZE);
        skyLight.fill((byte) 0xf);
    }

    /**
     * Create a ChunkSection with the specified chunk data. This
     * ChunkSection assumes ownership of the arrays passed in, and they
     * should not be further modified.
     *
     * @param types An array of block types for this chunk section.
     * @param skyLight An array for skylight data for this chunk section.
     * @param blockLight An array for blocklight data for this chunk section.
     */
    public ChunkSection(char[] types, NibbleArray skyLight, NibbleArray blockLight) {
        if (types.length != ARRAY_SIZE || skyLight.size() != ARRAY_SIZE || blockLight.size() != ARRAY_SIZE) {
            throw new IllegalArgumentException("An array length was not " + ARRAY_SIZE + ": " + types.length + " " + skyLight.size() + " " + blockLight.size());
        }
        this.types = types;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
        recount();
    }

    /**
     * Calculate the index into internal arrays for the given coordinates.
     *
     * @param x The x coordinate, for east and west.
     * @param y The y coordinate, for up and down.
     * @param z The z coordinate, for north and south.
     *
     * @return The index.
     */
    public int index(int x, int y, int z) {
        if (x < 0 || z < 0 || x >= GlowChunk.WIDTH || z >= GlowChunk.HEIGHT) {
            throw new IndexOutOfBoundsException("Coords (x=" + x + ",z=" + z + ") out of section bounds");
        }
        return (y & 0xf) << 8 | z << 4 | x;
    }

    /**
     * Recount the amount of non-air blocks in the chunk section.
     */
    public void recount() {
        count = 0;
        for (char type : types) {
            if (type != 0) {
                count++;
            }
        }
    }

    /**
     * Take a snapshot of this section which will not reflect future changes.
     *
     * @return The snapshot for this section.
     */
    public ChunkSection snapshot() {
        return new ChunkSection(types.clone(), skyLight.snapshot(), blockLight.snapshot());
    }
}