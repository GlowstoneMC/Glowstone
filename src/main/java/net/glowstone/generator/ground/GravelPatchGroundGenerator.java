package net.glowstone.generator.ground;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public class GravelPatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {
        if (surfaceNoise < -1.0D || surfaceNoise > 2.0D) {
            setTopMaterial(Material.GRAVEL);
            setGroundMaterial(Material.GRAVEL);
        } else {
            setTopMaterial(Material.GRASS);
            setGroundMaterial(Material.DIRT);
        }
        super.generateTerrainColumn(buf, world, random, x, z, biome, surfaceNoise);
    }
}
