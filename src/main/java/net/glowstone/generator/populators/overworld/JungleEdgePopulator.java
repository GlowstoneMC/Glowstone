package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BigOakTree;
import net.glowstone.generator.objects.trees.CocoaTree;
import net.glowstone.generator.objects.trees.JungleBush;
import org.bukkit.block.Biome;

public class JungleEdgePopulator extends JunglePopulator {

    private static final Biome[] BIOMES = {Biome.JUNGLE_EDGE, Biome.MUTATED_JUNGLE_EDGE};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BigOakTree.class, 10),
        new TreeDecoration(JungleBush.class, 50), new TreeDecoration(CocoaTree.class, 45)};

    public JungleEdgePopulator() {
        treeDecorator.setAmount(2);
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
