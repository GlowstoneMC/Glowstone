package net.glowstone.generator.populators;

import net.glowstone.generator.decorators.*;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomePopulator extends BlockPopulator {

    private final List<BlockPopulator> decorators = new ArrayList<BlockPopulator>();

    public BiomePopulator() {

        // the order is important

        addDecorator(new FlowerDecorator()
                .setDefaultFlowerWeight(2, FlowerDecorator.Flower.DANDELION)
                .setDefaultFlowerWeight(1, FlowerDecorator.Flower.POPPY)

                .setFlowerWeight(4, FlowerDecorator.Flower.DANDELION, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(2, FlowerDecorator.Flower.POPPY, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, FlowerDecorator.Flower.LILAC, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, FlowerDecorator.Flower.ROSE_BUSH, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, FlowerDecorator.Flower.PEONIA, Biome.OCEAN) // fix for lack of biomes

                .setDefaultAmount(2));

        addDecorator(new TallGrassDecorator()
                .setFernDensity(0.8D, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE)
                .setFernDensity(0.25D, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.COLD_TAIGA,
                        Biome.COLD_TAIGA_HILLS, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS)

                .setDefaultAmount(1)
                .setBiomeAmount(2, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(2, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST,
                        Biome.BIRCH_FOREST_HILLS, Biome.ROOFED_FOREST)
                .setBiomeAmount(25, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE)
                .setBiomeAmount(10, Biome.PLAINS)
                .setBiomeAmount(20, Biome.SAVANNA, Biome.SAVANNA_PLATEAU)
                .setBiomeAmount(5, Biome.SWAMPLAND)
                .setBiomeAmount(7, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.COLD_TAIGA,
                        Biome.COLD_TAIGA_HILLS, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS));

        addDecorator(new DeadBushDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(2, Biome.DESERT, Biome.DESERT_HILLS)
                .setBiomeAmount(20, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST)
                .setBiomeAmount(1, Biome.SWAMPLAND)
                .setBiomeAmount(1, Biome.TAIGA));

        addDecorator(new WaterLilyDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(4, Biome.SWAMPLAND));

        addDecorator(new SugarCaneDecorator()
                .setDefaultAmount(10)
                .setBiomeAmount(60, Biome.DESERT, Biome.DESERT_HILLS)
                .setBiomeAmount(13, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST)
                .setBiomeAmount(20, Biome.SWAMPLAND));

        addDecorator(new MelonDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE));

        addDecorator(new PumpkinDecorator());

        addDecorator(new CactusDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(5, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(10, Biome.DESERT, Biome.DESERT_HILLS)
                .setBiomeAmount(5, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST));
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        for (BlockPopulator decorator : decorators) {
            decorator.populate(world, random, source);
        }
    }

    private void addDecorator(BlockPopulator decorator) {
        decorators.add(decorator);
    }
}
