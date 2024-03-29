package net.glowstone.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class WoodedMountainsPopulator extends MountainsPopulator {

    private static final Biome[] BIOMES = {};

    public WoodedMountainsPopulator() {
        treeDecorator.setAmount(3);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
