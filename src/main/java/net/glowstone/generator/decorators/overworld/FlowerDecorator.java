package net.glowstone.generator.decorators.overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.Flower;
import net.glowstone.generator.objects.FlowerType;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class FlowerDecorator extends BlockDecorator {

    private final List<FlowerDecoration> defaultFlowers = new ArrayList<>();
    private final Map<Biome, List<FlowerDecoration>> biomesFlowers = new HashMap<>();

    public final FlowerDecorator setDefaultFlowerWeight(int weight, FlowerType flower) {
        defaultFlowers.add(new FlowerDecoration(flower, weight));
        return this;
    }

    public final FlowerDecorator setFlowerWeight(int weight, FlowerType flower, Biome... biomes) {
        for (Biome biome : biomes) {
            if (biomesFlowers.containsKey(biome)) {
                biomesFlowers.get(biome).add(new FlowerDecoration(flower, weight));
            } else {
                final List<FlowerDecoration> decorations = new ArrayList<>();
                decorations.add(new FlowerDecoration(flower, weight));
                biomesFlowers.put(biome, decorations);
            }
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) + 32);

        // the flower can change on each decoration pass
        FlowerType flower = null;
        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesFlowers.containsKey(biome)) {
            flower = getRandomFlower(random, biomesFlowers.get(biome));
        } else {
            flower = getRandomFlower(random, defaultFlowers);
        }
        if (flower == null) {
            return;
        }
        new Flower(flower).generate(world, random, sourceX, sourceY, sourceZ);
    }

    private FlowerType getRandomFlower(Random random, List<FlowerDecoration> decorations) {
        int totalWeight = 0;
        for (FlowerDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (FlowerDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getFlower();
            }
        }
        return null;
    }

    static class FlowerDecoration {

        private final FlowerType flower;
        private final int weight;

        public FlowerDecoration(FlowerType flower, int weight) {
            this.flower = flower;
            this.weight = weight;
        }

        public FlowerType getFlower() {
            return flower;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
