package net.glowstone.generator.ground;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import java.util.Random;

public class DirtPatchGroundGenerator extends GroundGenerator {

    @Override
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.75D) {
            setTopMaterial(COARSE_DIRT);
        } else if (surfaceNoise > -0.95D) {
            setTopMaterial(PODZOL);
        } else {
            setTopMaterial(GRASS);
        }
        setGroundMaterial(DIRT);

        super.generateTerrainColumn(chunkData, world, random, x, z, biome, surfaceNoise);
    }
}
