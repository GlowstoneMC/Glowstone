package net.glowstone.generator.populators;

import net.glowstone.generator.decorators.overworld.*;
import net.glowstone.generator.decorators.overworld.FlowerDecorator.Flower;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomePopulator extends BlockPopulator {

    private final List<BlockPopulator> decorators = new ArrayList<>();

    public BiomePopulator() {

        // the order is important

        addDecorator(new UnderwaterDecorator(Material.SAND)
                .setRadiuses(7, 2)
                .setDefaultAmount(3)
                .setBiomeAmount(0, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new UnderwaterDecorator(Material.CLAY)
                .setRadiuses(4, 1)
                .setPreservesShoreBlocks()
                .setDefaultAmount(1));

        addDecorator(new UnderwaterDecorator(Material.GRAVEL)
                .setRadiuses(6, 2)
                .setDefaultAmount(1)
                .setBiomeAmount(0, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new TreeDecorator()
                .setTreeWeight(3, TreeType.TREE, Biome.OCEAN) // fix for lack of biomes
                .setTreeWeight(2, TreeType.BIRCH, Biome.OCEAN) // fix for lack of biomes

                .setDefaultAmount(0)
                .setBiomeAmount(5, Biome.OCEAN)); // fix for lack of biomes

        addDecorator(new FlowerDecorator()
                .setDefaultFlowerWeight(2, Flower.DANDELION)
                .setDefaultFlowerWeight(1, Flower.POPPY)

                .setFlowerWeight(4, Flower.DANDELION, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(2, Flower.POPPY, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, Flower.LILAC, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, Flower.ROSE_BUSH, Biome.OCEAN) // fix for lack of biomes
                .setFlowerWeight(1, Flower.PEONIA, Biome.OCEAN) // fix for lack of biomes

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

        addDecorator(new MushroomDecorator(Material.BROWN_MUSHROOM)
                .setFixedHeightRange()
                .setDensity(0.25D)
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE)
                .setBiomeAmount(3, Biome.TAIGA, Biome.TAIGA_MOUNTAINS, Biome.TAIGA_MOUNTAINS)
                .setBiomeAmount(8, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));               

        addDecorator(new MushroomDecorator(Material.RED_MUSHROOM)
                .setDensity(0.125D)
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE)
                .setBiomeAmount(3, Biome.TAIGA, Biome.TAIGA_MOUNTAINS, Biome.TAIGA_MOUNTAINS)
                .setBiomeAmount(8, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));               

        addDecorator(new MushroomDecorator(Material.BROWN_MUSHROOM)
                .setDensity(0.25D)
                .setDefaultAmount(1));

        addDecorator(new MushroomDecorator(Material.RED_MUSHROOM)
                .setDensity(0.125D)
                .setDefaultAmount(1));

        addDecorator(new SugarCaneDecorator()
                .setDefaultAmount(10)
                .setBiomeAmount(60, Biome.DESERT, Biome.DESERT_HILLS)
                .setBiomeAmount(13, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST)
                .setBiomeAmount(20, Biome.SWAMPLAND));

        addDecorator(new PumpkinDecorator());

        addDecorator(new CactusDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(5, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(10, Biome.DESERT, Biome.DESERT_HILLS)
                .setBiomeAmount(5, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST));

        addDecorator(new FlowingLiquidDecorator(Material.WATER)
                .setDefaultAmount(50));

        addDecorator(new FlowingLiquidDecorator(Material.LAVA)
                .setDefaultAmount(20));

        addDecorator(new MelonDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE));

        addDecorator(new EmeraldOreDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS, Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS));

        addDecorator(new InfestedStoneDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS, Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS));
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
