package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import net.glowstone.generator.decorators.overworld.MushroomDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.RedwoodTree;
import net.glowstone.generator.objects.trees.TallRedwoodTree;
import org.bukkit.Chunk;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class TaigaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS, Biome.COLD_TAIGA,
            Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS};
    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {new DoublePlantDecoration(DoublePlantSpecies.LARGE_FERN, 1)};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree.class, 2), new TreeDecoration(TallRedwoodTree.class, 1)};

    protected final MushroomDecorator taigaBrownMushroomDecorator = new MushroomDecorator(Material.BROWN_MUSHROOM);
    protected final MushroomDecorator taigaRedMushroomDecorator = new MushroomDecorator(Material.RED_MUSHROOM);

    public TaigaPopulator() {
        super();
        doublePlantDecorator.setAmount(7);
        doublePlantDecorator.setDoublePlants(DOUBLE_PLANTS);
        treeDecorator.setAmount(10);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setFernDensity(0.8D);
        deadBushDecorator.setAmount(1);
        taigaBrownMushroomDecorator.setAmount(1);
        taigaBrownMushroomDecorator.setUseFixedHeightRange();
        taigaBrownMushroomDecorator.setDensity(0.25D);
        taigaRedMushroomDecorator.setAmount(1);
        taigaRedMushroomDecorator.setDensity(0.125D);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        taigaBrownMushroomDecorator.populate(world, random, chunk);
        taigaRedMushroomDecorator.populate(world, random, chunk);
    }
}
