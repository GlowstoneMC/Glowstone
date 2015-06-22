package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.TallBirchTree;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BirchForestMountainsPopulator extends BirchForestPopulator {

    private static final Biome[] BIOMES = {Biome.BIRCH_FOREST_MOUNTAINS, Biome.BIRCH_FOREST_HILLS_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BirchTree.class, 1), new TreeDecoration(TallBirchTree.class, 1)};

    public BirchForestMountainsPopulator() {
        super();
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
