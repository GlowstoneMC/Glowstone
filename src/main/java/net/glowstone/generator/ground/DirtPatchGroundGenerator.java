package net.glowstone.generator.ground;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class DirtPatchGroundGenerator extends GroundGenerator {

    @Override
    public void generateTerrainColumn(ChunkData chunkData, World world, Random random, int x, int z,
        Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.75D) {
            setTopMaterial(Material.COARSE_DIRT);
        } else if (surfaceNoise > -0.95D) {
            setTopMaterial(Material.PODZOL);
        } else {
            setTopMaterial(Material.GRASS);
        }
        setGroundMaterial(Material.DIRT);

        super.generateTerrainColumn(chunkData, world, random, x, z, biome, surfaceNoise);
    }
}
