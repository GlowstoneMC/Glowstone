package net.glowstone.generator.decorators.overworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoublePlant;
import net.glowstone.generator.objects.DoublePlantType;
import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;

public class ForestDecorator extends BlockDecorator {

    private static final FlowerType[] flowers = {FlowerType.POPPY, FlowerType.POPPY, FlowerType.ALLIUM, FlowerType.HOUSTONIA,
        FlowerType.TULIP_RED, FlowerType.TULIP_ORANGE, FlowerType.TULIP_WHITE, FlowerType.TULIP_PINK, FlowerType.OXEYE_DAISY};
    private static final DoublePlantType[] doublePlants = {DoublePlantType.LILAC, DoublePlantType.ROSE_BUSH, DoublePlantType.PEONIA};
    private final Map<Biome, Integer> biomesAmounts = new HashMap<>();
    private final PerlinNoiseGenerator noiseGen = new PerlinNoiseGenerator(new Random(2345));

    public final ForestDecorator setBiomeLoweringAmount(int amount, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesAmounts.put(biome, amount);
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4);
        int sourceZ = (source.getZ() << 4);
        int amount = random.nextInt(5);
        final Biome biome = world.getBiome(sourceX + 8, sourceZ + 8);
        if (biomesAmounts.containsKey(biome)) {
            amount -= biomesAmounts.get(biome);
        }
        int i = 0;
        while (i < amount) {
            for (int j = 0; j < 5; j++, i++) {
                int x = sourceX + random.nextInt(16);
                int z = sourceZ + random.nextInt(16);
                int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
                final DoublePlantType plantType = doublePlants[random.nextInt(doublePlants.length)];
                if (new DoublePlant(plantType).generate(world, random, x, y, z)) {
                    i++;
                    break;
                }
            }
        }

        for (i = 0; i < 100; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
            double noise = Math.max(0.0D, Math.min(0.9999D,
                    (noiseGen.noise((double) x / 48.0D, (double) z / 48.0D) + 1.0D) / 2.0D));
            final FlowerType flower = flowers[(int) (noise * flowers.length)];            
            new Flower(flower).generate(world, random, x, y, z);
        }
    }
}
