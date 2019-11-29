package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.BrownMushroomTree;
import net.glowstone.generator.objects.trees.DarkOakTree;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.generator.objects.trees.RedMushroomTree;
import org.bukkit.block.Biome;

public class DarkForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.DARK_FOREST, Biome.DARK_FOREST_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree::new, 20),
        new TreeDecoration(BirchTree::new, 5),
        new TreeDecoration(RedMushroomTree::new, 2),
        new TreeDecoration(BrownMushroomTree::new, 2), new TreeDecoration(DarkOakTree::new, 50)};

    /**
     * Creates a populator specialized for the Roofed Forest and Roofed Forest M biomes.
     */
    public DarkForestPopulator() {
        treeDecorator.setAmount(50);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(4);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
