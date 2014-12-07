package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.GrassSpecies;
import org.bukkit.World;
import org.bukkit.material.LongGrass;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoubleTallPlant;
import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;
import net.glowstone.generator.objects.TallGrass;

public class PlainsDecorator extends BlockDecorator {

    private static final FlowerType[] FLOWERS = {FlowerType.POPPY, FlowerType.HOUSTONIA, FlowerType.OXEYE_DAISY};
    private static final FlowerType[] TULIPS = {FlowerType.TULIP_RED, FlowerType.TULIP_ORANGE, FlowerType.TULIP_WHITE, FlowerType.TULIP_PINK}; 
    private final PerlinNoiseGenerator noiseGen = new PerlinNoiseGenerator(new Random(2345));

    @Override
    public void decorate(World world, Random random, Chunk source) {

        int sourceX = (source.getX() << 4);
        int sourceZ = (source.getZ() << 4);

        int flowerAmount = 15;
        int tallGrassAmount = 5;
        if (noiseGen.noise((double) (sourceX + 8) / 200.0D, (double) (sourceZ + 8) / 200.0D) >= -0.8D) {
            flowerAmount = 4;
            tallGrassAmount = 10;
            for (int i = 0; i < 7; i++) {
                int x = sourceX + random.nextInt(16);
                int z = sourceZ + random.nextInt(16);
                int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
                new DoubleTallPlant(DoublePlantSpecies.DOUBLE_TALLGRASS).generate(world, random, x, y, z);
            }
        }

        FlowerType flower = FlowerType.DANDELION;
        if (random.nextInt(3) > 0 && noiseGen.noise((double) (sourceX + 8) / 200.0D, (double) (sourceZ + 8) / 200.0D) >= -0.8D) {
            flower = FLOWERS[random.nextInt(FLOWERS.length)];
        } else {
            flower = TULIPS[random.nextInt(TULIPS.length)];
        }
        for (int i = 0; i < flowerAmount; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
            new Flower(flower).generate(world, random, x, y, z);
        }

        for (int i = 0; i < tallGrassAmount; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(world.getHighestBlockYAt(x, z) << 1);
            new TallGrass(new LongGrass(GrassSpecies.NORMAL)).generate(world, random, x, y, z);
        }
    }
}
