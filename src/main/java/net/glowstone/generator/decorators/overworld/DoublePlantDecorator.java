package net.glowstone.generator.decorators.overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoublePlant;
import net.glowstone.generator.objects.DoublePlantType;

public class DoublePlantDecorator extends BlockDecorator {

    private final Map<Biome, List<DoublePlantDecoration>> biomesDoublePlants = new HashMap<>();

    public final DoublePlantDecorator setDoublePlantWeight(int weight, DoublePlantType plantType, Biome... biomes) {
        for (Biome biome : biomes) {
            if (biomesDoublePlants.containsKey(biome)) {
                biomesDoublePlants.get(biome).add(new DoublePlantDecoration(plantType, weight));
            } else {
                final List<DoublePlantDecoration> decorations = new ArrayList<>();
                decorations.add(new DoublePlantDecoration(plantType, weight));
                biomesDoublePlants.put(biome, decorations);
            }
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) + 32);

        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesDoublePlants.containsKey(biome)) {
            final DoublePlantType plantType = getRandomDoublePlant(random, biomesDoublePlants.get(biome));
            new DoublePlant(plantType).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }

    private DoublePlantType getRandomDoublePlant(Random random, List<DoublePlantDecoration> decorations) {
        int totalWeight = 0;
        for (DoublePlantDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (DoublePlantDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getDoublePlant();
            }
        }
        return null;
    }

    static class DoublePlantDecoration {

        private final DoublePlantType plantType;
        private final int weight;

        public DoublePlantDecoration(DoublePlantType plantType, int weight) {
            this.plantType = plantType;
            this.weight = weight;
        }

        public DoublePlantType getDoublePlant() {
            return plantType;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
