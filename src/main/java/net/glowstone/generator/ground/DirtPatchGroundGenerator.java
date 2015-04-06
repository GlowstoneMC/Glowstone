package net.glowstone.generator.ground;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class DirtPatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.75D) {
            setTopMaterial(Material.DIRT, 1); // coarse dirt
        } else if (surfaceNoise > -0.95D) {
            setTopMaterial(Material.DIRT, 2); // podzol
        } else {
            setTopMaterial(Material.GRASS);
        }
        setGroundMaterial(Material.DIRT);

        super.generateTerrainColumn(buf, world, random, x, z, biome, surfaceNoise);
    }
}
