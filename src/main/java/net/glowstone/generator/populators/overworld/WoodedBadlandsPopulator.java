package net.glowstone.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class WoodedBadlandsPopulator extends BadlandsPopulator {

    private static final Biome[] BIOMES = {};

    public WoodedBadlandsPopulator() {
        treeDecorator.setAmount(5);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
