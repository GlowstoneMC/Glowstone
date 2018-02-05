package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class OceanPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.DEEP_OCEAN, Biome.OCEAN};

    /**
     * Creates a populator specialized for the ocean.
     */
    public OceanPopulator() {
        surfaceCaveDecorator.setAmount(0);
        entityDecorators.clear();
        // todo: squid decorator
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
