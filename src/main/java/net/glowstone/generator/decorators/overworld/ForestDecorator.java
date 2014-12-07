package net.glowstone.generator.decorators.overworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoubleTallPlant;
import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;

public class ForestDecorator extends BlockDecorator {

    private static final FlowerType[] FLOWERS = {FlowerType.POPPY, FlowerType.POPPY, FlowerType.ALLIUM, FlowerType.HOUSTONIA,
        FlowerType.TULIP_RED, FlowerType.TULIP_ORANGE, FlowerType.TULIP_WHITE, FlowerType.TULIP_PINK, FlowerType.OXEYE_DAISY};
    private static final DoublePlantSpecies[] DOUBLE_PLANTS = {DoublePlantSpecies.LILAC, DoublePlantSpecies.ROSE_BUSH, DoublePlantSpecies.PEONY};
    private final Map<Biome, Integer> biomesDoublePlantAmounts = new HashMap<>();
    private final Map<Biome, Integer> biomesFlowerAmounts = new HashMap<>();
    private final PerlinNoiseGenerator noiseGen = new PerlinNoiseGenerator(new Random(2345));

    public final ForestDecorator setBiomeLoweringAmount(int amount, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesDoublePlantAmounts.put(biome, amount);
        }
        return this;
    }

    public final ForestDecorator setBiomeFlowerAmount(int amount, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesFlowerAmounts.put(biome, amount);
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4);
        int sourceZ = (source.getZ() << 4);
        int amount = random.nextInt(5);
        final Biome biome = world.getBiome(sourceX + 8, sourceZ + 8);
        if (biomesDoublePlantAmounts.containsKey(biome)) {
            amount -= biomesDoublePlantAmounts.get(biome);
        }
        int i = 0;
        while (i < amount) {
            for (int j = 0; j < 5; j++, i++) {
                int x = sourceX + random.nextInt(16);
                int z = sourceZ + random.nextInt(16);
                int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
                final DoublePlantSpecies species = DOUBLE_PLANTS[random.nextInt(DOUBLE_PLANTS.length)];
                if (new DoubleTallPlant(species).generate(world, random, x, y, z)) {
                    i++;
                    break;
                }
            }
        }

        if (biomesFlowerAmounts.containsKey(biome)) {
            for (i = 0; i < biomesFlowerAmounts.get(biome); i++) {
                int x = sourceX + random.nextInt(16);
                int z = sourceZ + random.nextInt(16);
                int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
                double noise = Math.max(0.0D, Math.min(0.9999D,
                    (noiseGen.noise((double) x / 48.0D, (double) z / 48.0D) + 1.0D) / 2.0D));
                final FlowerType flower = FLOWERS[(int) (noise * FLOWERS.length)];            
                new Flower(flower).generate(world, random, x, y, z);
            }
        }
    }
}
