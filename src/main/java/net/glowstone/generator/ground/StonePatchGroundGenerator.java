package net.glowstone.generator.ground;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public class StonePatchGroundGenerator extends GroundGenerator {
    @Override
    public void generateTerrainColumn(short[][] buf, World world, Random random, int x, int z, Biome biome, double surfaceNoise) {
        if (surfaceNoise > 1.0D) {
            setTopMaterial(Material.STONE);
            setGroundMaterial(Material.STONE);
        } else {
            setTopMaterial(Material.GRASS);
            setGroundMaterial(Material.DIRT);
        }
        super.generateTerrainColumn(buf, world, random, x, z, biome, surfaceNoise);
    }
}
