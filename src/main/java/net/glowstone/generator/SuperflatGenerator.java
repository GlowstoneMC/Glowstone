package net.glowstone.generator;

import net.glowstone.generator.populators.StructurePopulator;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class SuperflatGenerator extends GlowChunkGenerator {

    public SuperflatGenerator() {
        new StructurePopulator();
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        final ChunkData chunkData = createChunkData(world);

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                generateTerrainColumn(chunkData, world, random, cx + x, cz + z);
            }
        }

        return chunkData;
    }

    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z) {
        x = x & 0xF;
        z = z & 0xF;

        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        chunkData.setBlock(x, 1, z, Material.DIRT);
        chunkData.setBlock(x, 2, z, Material.DIRT);
        chunkData.setBlock(x, 3, z, Material.GRASS);
    }
}
