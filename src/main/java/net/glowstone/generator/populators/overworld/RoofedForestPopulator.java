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

public class RoofedForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.ROOFED_FOREST, Biome.MUTATED_ROOFED_FOREST};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree.class, 20),
        new TreeDecoration(BirchTree.class, 5),
        new TreeDecoration(RedMushroomTree.class, 2),
        new TreeDecoration(BrownMushroomTree.class, 2), new TreeDecoration(DarkOakTree.class, 50)};

    /**
     * Creates a populator specialized for the Roofed Forest and Roofed Forest M biomes.
     */
    public RoofedForestPopulator() {
        treeDecorator.setAmount(50);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(4);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
