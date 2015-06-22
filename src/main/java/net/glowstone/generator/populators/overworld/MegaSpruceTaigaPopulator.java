package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.MegaSpruceTree;
import net.glowstone.generator.objects.trees.RedwoodTree;
import net.glowstone.generator.objects.trees.TallRedwoodTree;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MegaSpruceTaigaPopulator extends MegaTaigaPopulator {

    private static final Biome[] BIOMES = {Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree.class, 44), new TreeDecoration(TallRedwoodTree.class, 22),
        new TreeDecoration(MegaSpruceTree.class, 33)};

    public MegaSpruceTaigaPopulator() {
        super();
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
