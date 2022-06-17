package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.MegaSpruceTree;
import net.glowstone.generator.objects.trees.RedwoodTree;
import net.glowstone.generator.objects.trees.TallRedwoodTree;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class GiantSpruceTaigaPopulator extends GiantTreeTaigaPopulator {

    private static final Biome[] BIOMES = {};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree::new, 44),
        new TreeDecoration(TallRedwoodTree::new, 22),
        new TreeDecoration(MegaSpruceTree::new, 33)};

    public GiantSpruceTaigaPopulator() {
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
