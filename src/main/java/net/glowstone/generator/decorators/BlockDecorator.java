package net.glowstone.generator.decorators;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

public abstract class BlockDecorator extends BlockPopulator {

    private int defaultAmount;
    private final Map<Biome, Integer> biomesDecorations = new HashMap<>();

    public final BlockDecorator setDefaultAmount(int amount) {
        defaultAmount = amount;
        return this;
    }

    public final BlockDecorator setBiomeAmount(int amount, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesDecorations.put(biome, amount);
        }
        return this;
    }

    protected int getBiomeAmount(World world, Chunk chunk) {
        int amount = defaultAmount;
        final Biome biome = world.getBiome(chunk.getX() << 4, chunk.getZ() << 4);
        if (biomesDecorations.containsKey(biome)) {
            amount = biomesDecorations.get(biome);
        }
        return amount;
    }

    public abstract void decorate(World world, Random random, Chunk chunk);

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int i = 0; i < getBiomeAmount(world, chunk); i++) {
            decorate(world, random, chunk);
        }
    }
}
