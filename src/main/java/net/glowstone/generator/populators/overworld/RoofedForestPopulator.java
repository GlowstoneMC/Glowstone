package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.*;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class RoofedForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.ROOFED_FOREST};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree.class, 20), new TreeDecoration(BirchTree.class, 5),
            new TreeDecoration(RedMushroomTree.class, 2), new TreeDecoration(BrownMushroomTree.class, 2), new TreeDecoration(DarkOakTree.class, 50)};

    public RoofedForestPopulator() {
        super();
        treeDecorator.setAmount(50);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(4);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
