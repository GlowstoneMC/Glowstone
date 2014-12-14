package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.glowstone.generator.decorators.overworld.SnowDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.RedwoodTree;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class IcePlainsPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.ICE_PLAINS, Biome.ICE_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree.class, 1)};

    protected final SnowDecorator snowDecorator = new SnowDecorator();

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

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        snowDecorator.populate(world, random, chunk);
    }
}
