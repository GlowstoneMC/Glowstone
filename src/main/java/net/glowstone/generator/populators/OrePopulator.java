package net.glowstone.generator.populators;

import java.util.Random;

import net.glowstone.generator.OreVeinGenerator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * Populates the world with ores.
 */
public class OrePopulator extends BlockPopulator {

    private final OreVeinGenerator generator = new OreVeinGenerator();

    public OrePopulator() {
        generator.addOre(10, Material.DIRT, 0, 256, 32);
        generator.addOre(8, Material.GRAVEL, 0, 256, 32);
        generator.addOre(10, Material.STONE, 1, 0, 80, 32);
        generator.addOre(10, Material.STONE, 3, 0, 80, 32);
        generator.addOre(10, Material.STONE, 5, 0, 80, 32);
        generator.addOre(20, Material.COAL_ORE, 0, 128, 16);
        generator.addOre(20, Material.IRON_ORE, 0, 64, 8);
        generator.addOre(2, Material.GOLD_ORE, 0, 32, 8);
        generator.addOre(8, Material.REDSTONE_ORE, 0, 16, 7);
        generator.addOre(1, Material.DIAMOND_ORE, 0, 16, 7);
        generator.addOre(1, Material.LAPIS_ORE, 16, 16, 6);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        generator.generate(world, random, chunk);
    }
}
