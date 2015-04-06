package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.block.Biome;

public class ExtremeHillsPlusPopulator extends ExtremeHillsPopulator {

    private static final Biome[] BIOMES = {Biome.SMALL_MOUNTAINS, Biome.EXTREME_HILLS_PLUS, Biome.EXTREME_HILLS_PLUS_MOUNTAINS};

    public ExtremeHillsPlusPopulator() {
        super();
        treeDecorator.setAmount(3);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
