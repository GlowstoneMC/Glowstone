package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.glowstone.generator.decorators.overworld.EmeraldOreDecorator;
import net.glowstone.generator.decorators.overworld.InfestedStoneDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BigOakTree;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.generator.objects.trees.RedwoodTree;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class ExtremeHillsPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.MOUNTAINS, Biome.GRAVELLY_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree::new, 20),
        new TreeDecoration(BigOakTree::new, 1),
        new TreeDecoration(GenericTree::new, 9)};
    protected final EmeraldOreDecorator emeraldOreDecorator = new EmeraldOreDecorator();
    protected final InfestedStoneDecorator infestedStoneDecorator = new InfestedStoneDecorator();

    public ExtremeHillsPopulator() {
        treeDecorator.setAmount(0);
        treeDecorator.setTrees(TREES);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        emeraldOreDecorator.populate(world, random, chunk);
        infestedStoneDecorator.populate(world, random, chunk);
    }
}
