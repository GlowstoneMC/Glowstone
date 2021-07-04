package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BirchTree;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BirchForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BirchTree::new, 1)};

    public BirchForestPopulator() {
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
