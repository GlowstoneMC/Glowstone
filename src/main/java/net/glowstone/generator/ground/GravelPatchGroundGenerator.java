package net.glowstone.generator.ground;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class GravelPatchGroundGenerator extends GroundGenerator {

    @Override
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {
        if (surfaceNoise < -1.0D || surfaceNoise > 2.0D) {
            setTopMaterial(GRAVEL);
            setGroundMaterial(GRAVEL);
        } else {
            setTopMaterial(GRASS);
            setGroundMaterial(DIRT);
        }
        super.generateTerrainColumn(chunkData, world, random, x, z, biome, surfaceNoise);
    }
}
