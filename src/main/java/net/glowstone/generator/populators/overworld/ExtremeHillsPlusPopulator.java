package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class ExtremeHillsPlusPopulator extends ExtremeHillsPopulator {

    private static final Biome[] BIOMES = {Biome.SMALLER_EXTREME_HILLS,
        Biome.EXTREME_HILLS_WITH_TREES, Biome.MUTATED_EXTREME_HILLS_WITH_TREES};

    public ExtremeHillsPlusPopulator() {
        treeDecorator.setAmount(3);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
