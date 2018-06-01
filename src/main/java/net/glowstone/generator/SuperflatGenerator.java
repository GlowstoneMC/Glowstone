package net.glowstone.generator;

import java.util.Random;
import net.glowstone.generator.populators.StructurePopulator;
import org.bukkit.Material;
import org.bukkit.World;

public class SuperflatGenerator extends GlowChunkGenerator {

    public SuperflatGenerator() {
        super(new StructurePopulator());
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ,
        BiomeGrid biomes) {
        ChunkData chunkData = createChunkData(world);

        int cx = chunkX << 4;
        int cz = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                generateTerrainColumn(chunkData, world, random, cx + x, cz + z);
            }
        }

        return chunkData;
    }

    /**
     * Generates a terrain column.
     *
     * @param chunkData the chunk in which to generate
     * @param world the world (ignored)
     * @param random the PRNG (ignored)
     * @param x the column x coordinate
     * @param z the column z coordinate
     */
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x,
        int z) {
        // TODO: Handle superflat configurations.
        x = x & 0xF;
        z = z & 0xF;

        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        chunkData.setBlock(x, 1, z, Material.DIRT);
        chunkData.setBlock(x, 2, z, Material.DIRT);
        chunkData.setBlock(x, 3, z, Material.GRASS);
    }
}
