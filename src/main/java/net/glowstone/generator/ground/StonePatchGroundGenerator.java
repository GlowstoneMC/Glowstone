package net.glowstone.generator.ground;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class StonePatchGroundGenerator extends GroundGenerator {

    @Override
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.0D) {
            setTopMaterial(STONE);
            setGroundMaterial(STONE);
        } else {
            setTopMaterial(GRASS);
            setGroundMaterial(DIRT);
        }
        super.generateTerrainColumn(chunkData, world, random, x, z, biome, surfaceNoise);
    }
}
