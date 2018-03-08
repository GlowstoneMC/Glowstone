package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.TallBirchTree;
import org.bukkit.block.Biome;

public class BirchForestMountainsPopulator extends BirchForestPopulator {

    private static final Biome[] BIOMES = {Biome.MUTATED_BIRCH_FOREST,
        Biome.MUTATED_BIRCH_FOREST_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BirchTree::new, 1),
        new TreeDecoration(TallBirchTree::new, 1)};

    public BirchForestMountainsPopulator() {
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
