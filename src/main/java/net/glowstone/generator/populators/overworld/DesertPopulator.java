package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.block.Biome;

public class DesertPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.DESERT, Biome.DESERT_HILLS};

    public DesertPopulator() {
        super();
        waterLakeDecorator.setAmount(0);
        deadBushDecorator.setAmount(2);
        sugarCaneDecorator.setAmount(60);
        cactusDecorator.setAmount(10);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
