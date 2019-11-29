package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;
import net.glowstone.util.noise.SimplexOctaveGenerator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.OctaveGenerator;

public class FlowerForestPopulator extends ForestPopulator {

    public static final FlowerType[] FLOWERS = {FlowerType.POPPY, FlowerType.POPPY,
        FlowerType.DANDELION, FlowerType.ALLIUM,
        FlowerType.HOUSTONIA, FlowerType.TULIP_RED, FlowerType.TULIP_ORANGE, FlowerType.TULIP_WHITE,
        FlowerType.TULIP_PINK,
        FlowerType.OXEYE_DAISY};
    private final OctaveGenerator noiseGen;

    /**
     * Creates a populator for flower forests.
     */
    public FlowerForestPopulator() {
        treeDecorator.setAmount(6);
        flowerDecorator.setAmount(0);
        doublePlantLoweringAmount = 1;
        noiseGen = new SimplexOctaveGenerator(new Random(2345), 1);
        noiseGen.setScale(1 / 48.0D);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.FLOWER_FOREST));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);

        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;

        for (int i = 0; i < 100; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
            double noise = (noiseGen.noise(x, z, 0.5D, 2.0D) + 1.0D) / 2.0D;
            noise = noise < 0 ? 0 : noise > 0.9999D ? 0.9999D : noise;
            FlowerType flower = FLOWERS[(int) (noise * FLOWERS.length)];
            new Flower(flower).generate(world, random, x, y, z);
        }
    }
}
