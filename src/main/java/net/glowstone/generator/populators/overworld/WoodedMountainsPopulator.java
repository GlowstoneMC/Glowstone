package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class WoodedMountainsPopulator extends MountainsPopulator {

    private static final Biome[] BIOMES = {Biome.MOUNTAIN_EDGE,
        Biome.WOODED_MOUNTAINS, Biome.MODIFIED_GRAVELLY_MOUNTAINS};

    public WoodedMountainsPopulator() {
        treeDecorator.setAmount(3);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
