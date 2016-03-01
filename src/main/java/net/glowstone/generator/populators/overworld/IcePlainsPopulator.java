package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.RedwoodTree;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class IcePlainsPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.ICE_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree.class, 1)};

    public IcePlainsPopulator() {
        super();
        treeDecorator.setAmount(0);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(0);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
