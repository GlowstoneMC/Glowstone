package net.glowstone.generator.decorators.overworld;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Data;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.DoubleTallPlant;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

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

        Material species = getRandomDoublePlant(random, doublePlants);
        new DoubleTallPlant(species).generate(world, random, sourceX, sourceY, sourceZ);
    }

    private Material getRandomDoublePlant(Random random,
        List<DoublePlantDecoration> decorations) {
        int totalWeight = 0;
        for (DoublePlantDecoration decoration : decorations) {
            totalWeight += decoration.getWeight();
        }
        int weight = random.nextInt(totalWeight);
        for (DoublePlantDecoration decoration : decorations) {
            weight -= decoration.getWeight();
            if (weight < 0) {
                return decoration.getSpecies();
            }
        }
        return null;
    }

    @Data
    public static final class DoublePlantDecoration {
        private final Material species;
        private final int weight;
    }
}
