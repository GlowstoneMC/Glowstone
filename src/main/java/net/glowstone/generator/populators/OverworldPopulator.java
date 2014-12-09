package net.glowstone.generator.populators;

import net.glowstone.generator.decorators.overworld.*;
import net.glowstone.generator.objects.*;
import net.glowstone.generator.objects.trees.*;

import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverworldPopulator extends BlockPopulator {

    private final List<BlockPopulator> decorators = new ArrayList<>();

    public OverworldPopulator() {

        // the order is important

        addDecorator(new LakeDecorator(Material.STATIONARY_WATER)
                .setDefaultAmount(1)
                .setBiomeAmount(0, Biome.DESERT, Biome.DESERT_HILLS));

        addDecorator(new LakeDecorator(Material.STATIONARY_LAVA)
                .setDefaultAmount(1)
                .setBiomeAmount(0, Biome.DESERT, Biome.DESERT_HILLS));

        addDecorator(new UnderwaterDecorator(Material.SAND)
                .setRadiuses(7, 2)
                .setOverridableBlocks(Material.DIRT, Material.GRASS)
                .setDefaultAmount(3)
                .setBiomeAmount(0, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new UnderwaterDecorator(Material.CLAY)
                .setRadiuses(4, 1)
                .setOverridableBlocks(Material.DIRT)
                .setDefaultAmount(1));

        addDecorator(new UnderwaterDecorator(Material.GRAVEL)
                .setRadiuses(6, 2)
                .setOverridableBlocks(Material.DIRT, Material.GRASS)
                .setDefaultAmount(1)
                .setBiomeAmount(0, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new PlainsDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.PLAINS, Biome.SUNFLOWER_PLAINS));

        addDecorator(new ForestDecorator()
                .setBiomeLoweringAmount(3, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST,
                        Biome.BIRCH_FOREST_MOUNTAINS, Biome.BIRCH_FOREST_HILLS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS,
                        Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setBiomeLoweringAmount(1, Biome.FLOWER_FOREST)
                .setBiomeFlowerAmount(100, Biome.FLOWER_FOREST)
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_MOUNTAINS,
                        Biome.BIRCH_FOREST_HILLS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.ROOFED_FOREST,
                        Biome.ROOFED_FOREST_MOUNTAINS, Biome.FLOWER_FOREST));

        addDecorator(new StoneBoulderDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS));

        addDecorator(new IceDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.ICE_PLAINS_SPIKES));

        addDecorator(new DoublePlantDecorator()
                .setDoublePlantWeight(1, DoublePlantSpecies.LARGE_FERN, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS,
                        Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS, Biome.MEGA_TAIGA,
                        Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setDoublePlantWeight(1, DoublePlantSpecies.DOUBLE_TALLGRASS, Biome.SAVANNA, Biome.SAVANNA_MOUNTAINS,
                        Biome.SAVANNA_PLATEAU, Biome.SAVANNA_PLATEAU_MOUNTAINS)
                .setDoublePlantWeight(1, DoublePlantSpecies.SUNFLOWER, Biome.SUNFLOWER_PLAINS)
                .setDefaultAmount(0)
                .setBiomeAmount(7, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS,
                        Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS, Biome.MEGA_TAIGA,
                        Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS, Biome.SAVANNA,
                        Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU, Biome.SAVANNA_PLATEAU_MOUNTAINS)
                .setBiomeAmount(10, Biome.SUNFLOWER_PLAINS));

        addDecorator(new TreeDecorator()
                .setTreeWeight(3, GenericTree.class, Biome.OCEAN) // fix for lack of biomes
                .setTreeWeight(2, BirchTree.class, Biome.OCEAN) // fix for lack of biomes

                .setTreeWeight(20, RedwoodTree.class, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS)
                .setTreeWeight(1, GenericTree.class, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS) // BIG_TREE
                .setTreeWeight(9, GenericTree.class, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS)

                .setTreeWeight(4, GenericTree.class, Biome.FOREST, Biome.FOREST_HILLS, Biome.FLOWER_FOREST,
                        Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setTreeWeight(1, BirchTree.class, Biome.FOREST, Biome.FOREST_HILLS, Biome.FLOWER_FOREST,
                        Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_MOUNTAINS, Biome.BIRCH_FOREST_HILLS,
                        Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setTreeWeight(2, RedMushroomTree.class, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setTreeWeight(2, BrownMushroomTree.class, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setTreeWeight(76, DarkOakTree.class, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setTreeWeight(1, TallBirchTree.class, Biome.BIRCH_FOREST_MOUNTAINS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS)

                .setTreeWeight(2, RedwoodTree.class, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS,
                        Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS)
                .setTreeWeight(1, TallRedwoodTree.class, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS,
                        Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS)

                .setTreeWeight(52, RedwoodTree.class, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS)
                .setTreeWeight(36, MegaPineTree.class, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS)
                .setTreeWeight(26, TallRedwoodTree.class, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS)
                .setTreeWeight(3, MegaSpruceTree.class, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS)

                .setTreeWeight(44, RedwoodTree.class, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setTreeWeight(33, MegaSpruceTree.class, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setTreeWeight(22, TallRedwoodTree.class, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)

                .setTreeWeight(1, SwampTree.class, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS)

                .setTreeWeight(1, RedwoodTree.class, Biome.ICE_PLAINS, Biome.ICE_MOUNTAINS, Biome.ICE_PLAINS_SPIKES)

                .setTreeWeight(10, GenericTree.class, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS, // BIG_TREE
                        Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS)
                .setTreeWeight(45, JungleBush.class, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS,
                        Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS)
                .setTreeWeight(15, MegaJungleTree.class, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS)
                .setTreeWeight(30, CocoaTree.class, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS)
                .setTreeWeight(45, CocoaTree.class, Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS)

                .setTreeWeight(1, GenericTree.class, Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS)

                .setTreeWeight(1, GenericTree.class, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU_MOUNTAINS)
                .setTreeWeight(4, AcaciaTree.class, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU_MOUNTAINS)

                .setTreeWeight(1, RedMushroomTree.class, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE)
                .setTreeWeight(1, BrownMushroomTree.class, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE)

                .setDefaultAmount(Integer.MIN_VALUE)
                .setBiomeAmount(5, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(0, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS, Biome.ICE_PLAINS, Biome.ICE_MOUNTAINS)
                .setBiomeAmount(1, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE)
                .setBiomeAmount(2, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS, Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS,
                        Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU_MOUNTAINS)
                .setBiomeAmount(3, Biome.SMALL_MOUNTAINS, Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS)
                .setBiomeAmount(5, Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS)
                .setBiomeAmount(6, Biome.FLOWER_FOREST)
                .setBiomeAmount(10, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_MOUNTAINS,
                        Biome.BIRCH_FOREST_HILLS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.TAIGA, Biome.TAIGA_HILLS,
                        Biome.TAIGA_MOUNTAINS, Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS,
                        Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setBiomeAmount(50, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS)
                .setBiomeAmount(50, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS));

        addDecorator(new FlowerDecorator()
                .setDefaultFlowerWeight(2, FlowerType.DANDELION)
                .setDefaultFlowerWeight(1, FlowerType.POPPY)

                .setFlowerWeight(1, FlowerType.BLUE_ORCHID, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS)

                .setDefaultAmount(2)
                .setBiomeAmount(0, Biome.ICE_PLAINS, Biome.ICE_MOUNTAINS, Biome.ICE_PLAINS_SPIKES, Biome.MESA,
                        Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_MOUNTAINS, Biome.MESA_PLATEAU_FOREST,
                        Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.MESA_BRYCE, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE,
                        Biome.PLAINS, Biome.SUNFLOWER_PLAINS)
                .setBiomeAmount(0, Biome.FLOWER_FOREST)
                .setBiomeAmount(1, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS)
                .setBiomeAmount(4, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS,
                        Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU));

        addDecorator(new TallGrassDecorator()
                .setFernDensity(0.25D, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS,
                        Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS)
                .setFernDensity(0.8D, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS, Biome.COLD_TAIGA,
                        Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS,
                        Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)

                .setDefaultAmount(1)
                .setBiomeAmount(2, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(0, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE, Biome.PLAINS, Biome.SUNFLOWER_PLAINS, Biome.ICE_PLAINS_SPIKES)
                .setBiomeAmount(2, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_MOUNTAINS, 
                        Biome.BIRCH_FOREST_HILLS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.ROOFED_FOREST,
                        Biome.ROOFED_FOREST_MOUNTAINS)
                .setBiomeAmount(5, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS, Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU_MOUNTAINS)
                .setBiomeAmount(7, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setBiomeAmount(20, Biome.SAVANNA, Biome.SAVANNA_PLATEAU)
                .setBiomeAmount(25, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE));

        addDecorator(new DeadBushDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(2, Biome.DESERT, Biome.DESERT_MOUNTAINS, Biome.DESERT_HILLS)
                .setBiomeAmount(20, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_MOUNTAINS,
                        Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.MESA_BRYCE)
                .setBiomeAmount(1, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS)
                .setBiomeAmount(1, Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS));

        addDecorator(new WaterLilyDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(4, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new MushroomDecorator(Material.BROWN_MUSHROOM)
                .setFixedHeightRange()
                .setDensity(0.25D)
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE, Biome.TAIGA, Biome.TAIGA_HILLS,
                        Biome.TAIGA_MOUNTAINS, Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS)
                .setBiomeAmount(3, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setBiomeAmount(8, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));               

        addDecorator(new MushroomDecorator(Material.RED_MUSHROOM)
                .setDensity(0.125D)
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE, Biome.TAIGA, Biome.TAIGA_HILLS,
                        Biome.TAIGA_MOUNTAINS, Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS)
                .setBiomeAmount(3, Biome.MEGA_TAIGA, Biome.MEGA_TAIGA_HILLS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS)
                .setBiomeAmount(8, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));               

        addDecorator(new MushroomDecorator(Material.BROWN_MUSHROOM)
                .setDensity(0.25D)
                .setDefaultAmount(1));

        addDecorator(new MushroomDecorator(Material.RED_MUSHROOM)
                .setDensity(0.125D)
                .setDefaultAmount(1));

        addDecorator(new SugarCaneDecorator()
                .setDefaultAmount(10)
                .setBiomeAmount(60, Biome.DESERT, Biome.DESERT_HILLS, Biome.DESERT_MOUNTAINS)
                .setBiomeAmount(13, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_MOUNTAINS,
                        Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.MESA_BRYCE)
                .setBiomeAmount(20, Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));

        addDecorator(new PumpkinDecorator());

        addDecorator(new CactusDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(5, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(10, Biome.DESERT, Biome.DESERT_HILLS, Biome.DESERT_MOUNTAINS)
                .setBiomeAmount(5, Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_MOUNTAINS,
                        Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.MESA_BRYCE));

        addDecorator(new FlowingLiquidDecorator(Material.WATER)
                .setDefaultAmount(50));

        addDecorator(new FlowingLiquidDecorator(Material.LAVA)
                .setDefaultAmount(20));

        addDecorator(new MelonDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS,
                        Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS));

        addDecorator(new EmeraldOreDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS,
                        Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS));

        addDecorator(new InfestedStoneDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.OCEAN) // fix for lack of biomes
                .setBiomeAmount(1, Biome.EXTREME_HILLS, Biome.EXTREME_HILLS_MOUNTAINS,
                        Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS));

        addDecorator(new SnowDecorator()
                .setDefaultAmount(0)
                .setBiomeAmount(1, Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS, Biome.ICE_PLAINS,
                        Biome.ICE_MOUNTAINS, Biome.ICE_PLAINS_SPIKES, Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER, Biome.COLD_BEACH));
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
