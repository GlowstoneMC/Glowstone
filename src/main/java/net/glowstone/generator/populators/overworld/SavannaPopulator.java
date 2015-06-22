package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.AcaciaTree;
import net.glowstone.generator.objects.trees.GenericTree;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SavannaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.SAVANNA, Biome.SAVANNA_PLATEAU};
    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {new DoublePlantDecoration(DoublePlantSpecies.DOUBLE_TALLGRASS, 1)};
    private static final TreeDecoration[] TREES = {new TreeDecoration(AcaciaTree.class, 4), new TreeDecoration(GenericTree.class, 1)};

    public SavannaPopulator() {
        super();
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
