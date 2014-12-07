package net.glowstone.generator.decorators.overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.World;
import org.bukkit.block.Biome;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoubleTallPlant;

public class DoublePlantDecorator extends BlockDecorator {

    private final Map<Biome, List<DoublePlantDecoration>> biomesDoublePlants = new HashMap<>();

    public final DoublePlantDecorator setDoublePlantWeight(int weight, DoublePlantSpecies species, Biome... biomes) {
        for (Biome biome : biomes) {
            if (biomesDoublePlants.containsKey(biome)) {
                biomesDoublePlants.get(biome).add(new DoublePlantDecoration(species, weight));
            } else {
                final List<DoublePlantDecoration> decorations = new ArrayList<>();
                decorations.add(new DoublePlantDecoration(species, weight));
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
            final DoublePlantSpecies species = getRandomDoublePlant(random, biomesDoublePlants.get(biome));
            new DoubleTallPlant(species).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }

    private DoublePlantSpecies getRandomDoublePlant(Random random, List<DoublePlantDecoration> decorations) {
        int totalWeight = 0;
        for (DoublePlantDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (DoublePlantDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getSpecies();
            }
        }
        return null;
    }

    static class DoublePlantDecoration {

        private final DoublePlantSpecies species;
        private final int weight;

        public DoublePlantDecoration(DoublePlantSpecies species, int weight) {
            this.species = species;
            this.weight = weight;
        }

        public DoublePlantSpecies getSpecies() {
            return species;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
