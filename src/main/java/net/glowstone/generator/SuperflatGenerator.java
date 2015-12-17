package net.glowstone.generator;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;

import net.glowstone.generator.populators.StructurePopulator;

public class SuperflatGenerator extends GlowChunkGenerator {

    public SuperflatGenerator() {
        new StructurePopulator();
    }

    @Override
    public short[][] generateExtBlockSectionsWithData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        final short[][] buf = new short[16][];

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                generateTerrainColumn(buf, world, random, cx + x, cz + z);
            }
        }

        return buf;
    }

    @SuppressWarnings("deprecation")
    private void set(short[][] buf, int x, int y, int z, Material id) {
        if (buf[y >> 4] == null) {
            buf[y >> 4] = new short[4096];
        }
        buf[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (short) (id.getId() << 4);
    }

    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z) {
        x = x & 0xF;
        z = z & 0xF;

        set(buf, x, 0, z, Material.BEDROCK);
        set(buf, x, 1, z, Material.DIRT);
        set(buf, x, 2, z, Material.DIRT);
        set(buf, x, 3, z, Material.GRASS);
    }
}
