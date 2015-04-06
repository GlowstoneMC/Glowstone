package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.PerlinNoiseGenerator;

public class FlowerForestPopulator extends ForestPopulator {

    private static final FlowerType[] FLOWERS = {FlowerType.POPPY, FlowerType.POPPY, FlowerType.ALLIUM, FlowerType.HOUSTONIA,
        FlowerType.TULIP_RED, FlowerType.TULIP_ORANGE, FlowerType.TULIP_WHITE, FlowerType.TULIP_PINK, FlowerType.OXEYE_DAISY};
    private final PerlinNoiseGenerator noiseGen = new PerlinNoiseGenerator(new Random(2345));

    public FlowerForestPopulator() {
        super();
        treeDecorator.setAmount(6);
        flowerDecorator.setAmount(0);
        doublePlantLoweringAmount = 1;
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(new Biome[] {Biome.FLOWER_FOREST}));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);

        int sourceX = (chunk.getX() << 4);
        int sourceZ = (chunk.getZ() << 4);

        for (int i = 0; i < 100; i++) {
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
