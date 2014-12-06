package net.glowstone.generator.decorators.overworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoublePlant;
import net.glowstone.generator.objects.DoublePlantType;

public class ForestDecorator extends BlockDecorator {

    private static final DoublePlantType[] doublePlants = {DoublePlantType.LILAC, DoublePlantType.ROSE_BUSH, DoublePlantType.PEONIA};
    private final Map<Biome, Integer> biomesAmounts = new HashMap<>();

    public final ForestDecorator setBiomeMinAmount(int minAmount, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesAmounts.put(biome, minAmount);
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        final Biome biome = world.getBiome((source.getX() << 4) + 8, (source.getZ() << 4) + 8);
        if (biomesAmounts.containsKey(biome)) {
            int amount = random.nextInt(5) - biomesAmounts.get(biome);
            int i = 0;
            while (i < amount) {
                for (int j = 0; j < 5; j++, i++) {
                    int sourceX = (source.getX() << 4) + random.nextInt(16);
                    int sourceZ = (source.getZ() << 4) + random.nextInt(16);
                    int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) + 32);

                    final DoublePlantType plantType = doublePlants[random.nextInt(doublePlants.length)];
                    if (new DoublePlant(plantType).generate(world, random, sourceX, sourceY, sourceZ)) {
                        i++;
                        break;
                    }
                }
            }
        }
    }
}
