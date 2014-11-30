package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import net.glowstone.generator.OreVeinGenerator;
import net.glowstone.generator.decorators.BlockDecorator;

public class InfestedStoneDecorator extends BlockDecorator {

    private final OreVeinGenerator generator = new OreVeinGenerator();

    public InfestedStoneDecorator() {
        generator.addOre(7, Material.MONSTER_EGGS, 0, 64, 8);
    }

    @Override
    public void decorate(World world, Random random, Chunk chunk) {
        generator.generate(world, random, chunk);
    }
}
