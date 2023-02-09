package net.glowstone.chunk;

import lombok.Getter;
import net.glowstone.constants.GlowBiome;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing a snapshot of a chunk.
 */
public class GlowChunkSnapshot implements ChunkSnapshot {

    @Getter
    private final int x;
    @Getter
    private final int z;
    @Getter
    private final String worldName;
    @Getter
    private final long captureFullTime;

    /**
     * The ChunkSection array backing this snapshot. In general, it should not be modified
     * externally.
     *
     * @return The array of ChunkSections.
     */
    @Getter
    private final ChunkSection[] rawSections;

    private final byte[] height;
    private final double[] temp;
    private final double[] humid;
    @Getter
    private final byte[] rawBiomes;
    @Getter
    private final boolean isSlimeChunk;

    /**
     * Creates a snapshot of a chunk.
     *
     * @param x        the chunk x coordinate
     * @param z        the chunk z coordinate
     * @param world    the world the chunk is in
     * @param sections the chunk contents
     * @param height   the heightmap
     * @param biomes   the biome map
     * @param svTemp   if true, copy temperature and humidity from the world
     */
    public GlowChunkSnapshot(int x, int z, World world, ChunkSection[] sections, byte[] height,
                             byte[] biomes, boolean svTemp, boolean isSlimeChunk) {
        this.x = x;
        this.z = z;
        this.worldName = world.getName();
        captureFullTime = world.getFullTime();
        this.isSlimeChunk = isSlimeChunk;

        int numSections = sections != null ? sections.length : 0;
        this.rawSections = new ChunkSection[numSections];
        for (int i = 0; i < numSections; ++i) {
            if (sections[i] != null) {
                this.rawSections[i] = sections[i].snapshot();
            }
        }

        this.height = height;
        this.rawBiomes = biomes;

        if (svTemp) {
            int baseX = x << 4;
            int baseZ = z << 4;
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
        if (idx < 0 || idx >= rawSections.length) {
            return null;
        }
        return rawSections[idx];
    }

    /**
     * Returns the heightmap, converted to an {@code int[]}.
     *
     * @return the heightmap as an {@code int[]}
     */
    public int[] getRawHeightmap() {
        int[] result = new int[height.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = height[i];
        }
        return result;
    }

    @Override
    public boolean isSectionEmpty(int sy) {
        return sy < 0 || sy >= rawSections.length || rawSections[sy] == null;
    }

    @Override
    public boolean contains(@NotNull BlockData blockData) {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(@NotNull Biome biome) {
        return false;
    }

    public int getBlockTypeId(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) >> 4;
    }

    @Override
    public Material getBlockType(int x, int y, int z) {
        BlockData data = getBlockData(x, y, z);
        return data == null ? Material.AIR : data.getMaterial();
    }

    @Override
    public int getData(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) & 0xF;
    }

    @Override
    public BlockData getBlockData(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return section == null ? null : section.getBlockData(x, y, z);
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
        return GlowBiome.getBiome(rawBiomes[coordToIndex(x, z)]).getType();
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        // TODO: Support 3D biomes
        return getBiome(x, z);
    }

    @Override
    public double getRawBiomeTemperature(int x, int z) {
        return temp[coordToIndex(x, z)];
    }

    @Override
    public double getRawBiomeTemperature(int x, int y, int z) {
        // TODO: Support 3D biomes
        return getRawBiomeTemperature(x, z);
    }

    public double getRawBiomeRainfall(int x, int z) {
        return humid[coordToIndex(x, z)];
    }

    private int coordToIndex(int x, int z) {
        if (x < 0 || z < 0 || x >= GlowChunk.WIDTH || z >= GlowChunk.HEIGHT) {
            throw new IndexOutOfBoundsException();
        }

        return z * GlowChunk.WIDTH + x;
    }

    public static class EmptySnapshot extends GlowChunkSnapshot {

        public EmptySnapshot(int x, int z, World world, boolean svBiome, boolean svTemp) {
            super(x, z, world, null, null, svBiome ? new byte[256] : null, svTemp, false);
        }

        @Override
        public int getBlockTypeId(int x, int y, int z) {
            return 0;
        }

        @Override
        public Material getBlockType(int x, int y, int z) {
            return Material.AIR;
        }

        @Override
        public BlockData getBlockData(int x, int y, int z) {
            return null;
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
