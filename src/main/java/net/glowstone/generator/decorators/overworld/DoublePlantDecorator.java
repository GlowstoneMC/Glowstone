package net.glowstone.generator.decorators.overworld;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.World;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoubleTallPlant;

public class DoublePlantDecorator extends BlockDecorator {

    private List<DoublePlantDecoration> doublePlants;

    public final void setDoublePlants(DoublePlantDecoration... doublePlants) {
        this.doublePlants = Arrays.asList(doublePlants);
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) + 32);

        final DoublePlantSpecies species = getRandomDoublePlant(random, doublePlants);
        new DoubleTallPlant(species).generate(world, random, sourceX, sourceY, sourceZ);
    }

    private static DoublePlantSpecies getRandomDoublePlant(Random random, List<DoublePlantDecoration> decorations) {
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

    public static class DoublePlantDecoration {

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
