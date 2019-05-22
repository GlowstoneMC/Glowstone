package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.AcaciaTree;
import net.glowstone.generator.objects.trees.GenericTree;
import org.bukkit.Material;
import org.bukkit.block.Biome;

public class SavannaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.SAVANNA, Biome.SAVANNA_PLATEAU};
    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {
        new DoublePlantDecoration(Material.TALL_GRASS, 1)};
    private static final TreeDecoration[] TREES = {new TreeDecoration(AcaciaTree::new, 4),
        new TreeDecoration(GenericTree::new, 1)};

    /**
     * Creates a populator specialized for the Savanna and Savanna Plateau biomes.
     */
    public SavannaPopulator() {
        doublePlantDecorator.setAmount(7);
        doublePlantDecorator.setDoublePlants(DOUBLE_PLANTS);
        treeDecorator.setAmount(1);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(4);
        tallGrassDecorator.setAmount(20);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
