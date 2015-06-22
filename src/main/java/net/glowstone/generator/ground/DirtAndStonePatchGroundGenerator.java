package net.glowstone.generator.ground;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public class DirtAndStonePatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.75D) {
            setTopMaterial(Material.STONE);
            setGroundMaterial(Material.STONE);
        } else if (surfaceNoise > -0.5D) {
            setTopMaterial(Material.DIRT, 1); // coarse dirt
            setGroundMaterial(Material.DIRT);
        } else {
            setTopMaterial(Material.GRASS);
            setGroundMaterial(Material.DIRT);
        }

        super.generateTerrainColumn(buf, world, random, x, z, biome, surfaceNoise);
    }
}
