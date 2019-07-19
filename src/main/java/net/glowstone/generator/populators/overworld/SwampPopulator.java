package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.FlowerDecorator.FlowerDecoration;
import net.glowstone.generator.decorators.overworld.MushroomDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.decorators.overworld.WaterLilyDecorator;
import net.glowstone.generator.objects.FlowerType;
import net.glowstone.generator.objects.trees.SwampTree;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class SwampPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.SWAMP, Biome.SWAMP_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(SwampTree::new, 1)};
    private static final FlowerDecoration[] FLOWERS = {
        new FlowerDecoration(FlowerType.BLUE_ORCHID, 1)};

    protected final MushroomDecorator swamplandBrownMushroomDecorator = new MushroomDecorator(
        Material.BROWN_MUSHROOM);
    protected final MushroomDecorator swamplandRedMushroomDecorator = new MushroomDecorator(
        Material.RED_MUSHROOM);
    protected final WaterLilyDecorator waterlilyDecorator = new WaterLilyDecorator();

    /**
     * Creates a populator for the Swamp and Swamp M biomes.
     */
    public SwampPopulator() {
        sandPatchDecorator.setAmount(0);
        gravelPatchDecorator.setAmount(0);
        treeDecorator.setAmount(2);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(1);
        flowerDecorator.setFlowers(FLOWERS);
        tallGrassDecorator.setAmount(5);
        deadBushDecorator.setAmount(1);
        sugarCaneDecorator.setAmount(20);
        swamplandBrownMushroomDecorator.setAmount(8);
        swamplandRedMushroomDecorator.setAmount(8);
        waterlilyDecorator.setAmount(4);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        swamplandBrownMushroomDecorator.populate(world, random, chunk);
        swamplandRedMushroomDecorator.populate(world, random, chunk);
        waterlilyDecorator.populate(world, random, chunk);
    }
}
