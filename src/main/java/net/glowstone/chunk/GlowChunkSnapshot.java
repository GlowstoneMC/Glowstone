package net.glowstone.chunk;

import net.glowstone.constants.GlowBiome;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Class representing a snapshot of a chunk.
 */
public class GlowChunkSnapshot implements ChunkSnapshot {

    private final int x, z;
    private final String world;
    private final long time;

    private final ChunkSection[] sections;

    private final byte[] height;
    private final double[] temp, humid;
    private final byte[] biomes;

    public GlowChunkSnapshot(int x, int z, World world, ChunkSection[] sections, byte[] height, byte[] biomes, boolean svTemp) {
        this.x = x;
        this.z = z;
        this.world = world.getName();
        time = world.getFullTime();

        int numSections = sections != null ? sections.length : 0;
        this.sections = new ChunkSection[numSections];
        for (int i = 0; i < numSections; ++i) {
            if (sections[i] != null) {
                this.sections[i] = sections[i].snapshot();
            }
        }

        this.height = height;
        this.biomes = biomes;

        if (svTemp) {
            int baseX = x << 4, baseZ = z << 4;
            temp = new double[(16 << 4)];
            humid = new double[(16 << 4)];
            for (int xx = 0; xx < 16; ++xx) {
                for (int zz = 0; zz < 16; ++zz) {
                    temp[coordToIndex(xx, zz)] = world.getTemperature(baseX + xx, baseZ + zz);
                    humid[coordToIndex(xx, zz)] = world.getHumidity(baseX + xx, baseZ + zz);
                }
            }
        } else {
            temp = humid = null;
        }
    }

    private ChunkSection getSection(int y) {
        int idx = y >> 4;
        if (idx < 0 || idx >= sections.length) {
            return null;
        }
        return sections[idx];
    }

    /**
     * Get the ChunkSection array backing this snapshot. In general, it should not be modified.
     *
     * @return The array of ChunkSections.
     */
    public ChunkSection[] getRawSections() {
        return sections;
    }

    public int[] getRawHeightmap() {
        int[] result = new int[height.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = height[i];
        }
        return result;
    }

    public byte[] getRawBiomes() {
        return biomes;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public String getWorldName() {
        return world;
    }

    @Override
    public long getCaptureFullTime() {
        return time;
    }

    @Override
    public boolean isSectionEmpty(int sy) {
        return sy < 0 || sy >= sections.length || sections[sy] == null;
    }

    @Override
    public int getBlockTypeId(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) >> 4;
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) & 0xF;
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? ChunkSection.EMPTY_SKYLIGHT : section.getSkyLight(x, y, z);
    }

    @Override
    public int getBlockEmittedLight(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? ChunkSection.EMPTY_BLOCK_LIGHT : section.getBlockLight(x, y, z);
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return height[coordToIndex(x, z)];
    }

    @Override
    public Biome getBiome(int x, int z) {
        return GlowBiome.getBiome(biomes[coordToIndex(x, z)]);
    }

    @Override
    public double getRawBiomeTemperature(int x, int z) {
        return temp[coordToIndex(x, z)];
    }

    @Override
    public double getRawBiomeRainfall(int x, int z) {
        return humid[coordToIndex(x, z)];
    }

    private int coordToIndex(int x, int z) {
        if (x < 0 || z < 0 || x >= GlowChunk.WIDTH || z >= GlowChunk.HEIGHT)
            throw new IndexOutOfBoundsException();

        return z * GlowChunk.WIDTH + x;
    }

    public static class EmptySnapshot extends GlowChunkSnapshot {

        public EmptySnapshot(int x, int z, World world, boolean svBiome, boolean svTemp) {
            super(x, z, world, null, null, svBiome ? new byte[256] : null, svTemp);
        }

        @Override
        public int getBlockTypeId(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getBlockData(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getBlockSkyLight(int x, int y, int z) {
            return 15;
        }

        @Override
        public int getBlockEmittedLight(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getHighestBlockYAt(int x, int z) {
            return 0;
        }

    }

}
