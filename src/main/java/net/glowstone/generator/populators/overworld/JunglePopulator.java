package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.glowstone.generator.decorators.overworld.MelonDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BigOakTree;
import net.glowstone.generator.objects.trees.CocoaTree;
import net.glowstone.generator.objects.trees.JungleBush;
import net.glowstone.generator.objects.trees.MegaJungleTree;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class JunglePopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BigOakTree.class, 10),
        new TreeDecoration(JungleBush.class, 50), new TreeDecoration(MegaJungleTree.class, 15),
        new TreeDecoration(CocoaTree.class, 30)};

    private final MelonDecorator melonDecorator = new MelonDecorator();

    public JunglePopulator() {
        super();
        treeDecorator.setAmount(65);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(4);
        tallGrassDecorator.setAmount(25);
        tallGrassDecorator.setFernDensity(0.25D);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        super.populate(world, random, chunk);
        melonDecorator.populate(world, random, chunk);
    }
}
