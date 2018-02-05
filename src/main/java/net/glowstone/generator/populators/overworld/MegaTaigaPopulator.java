package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.glowstone.generator.decorators.overworld.StoneBoulderDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.MegaPineTree;
import net.glowstone.generator.objects.trees.MegaSpruceTree;
import net.glowstone.generator.objects.trees.RedwoodTree;
import net.glowstone.generator.objects.trees.TallRedwoodTree;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class MegaTaigaPopulator extends TaigaPopulator {

    private static final Biome[] BIOMES = {Biome.REDWOOD_TAIGA, Biome.REDWOOD_TAIGA_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree.class, 52),
        new TreeDecoration(TallRedwoodTree.class, 26),
        new TreeDecoration(MegaPineTree.class, 36), new TreeDecoration(MegaSpruceTree.class, 3)};

    protected final StoneBoulderDecorator stoneBoulderDecorator = new StoneBoulderDecorator();

    /**
     * Creates a populator specialized for the Mega Taiga and Mega Taiga Hills biomes.
     */
    public MegaTaigaPopulator() {
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(7);
        deadBushDecorator.setAmount(0);
        taigaBrownMushroomDecorator.setAmount(3);
        taigaRedMushroomDecorator.setAmount(3);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        stoneBoulderDecorator.populate(world, random, chunk);
        super.populateOnGround(world, random, chunk);
    }
}
