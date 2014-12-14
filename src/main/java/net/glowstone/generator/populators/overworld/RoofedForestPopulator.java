package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.block.Biome;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.BrownMushroomTree;
import net.glowstone.generator.objects.trees.DarkOakTree;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.generator.objects.trees.RedMushroomTree;

public class RoofedForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree.class, 4), new TreeDecoration(BirchTree.class, 1),
        new TreeDecoration(RedMushroomTree.class, 2), new TreeDecoration(BrownMushroomTree.class, 2), new TreeDecoration(DarkOakTree.class, 76)};

    public RoofedForestPopulator() {
        super();
        treeDecorator.setAmount(50);
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
