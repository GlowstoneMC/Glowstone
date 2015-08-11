package net.glowstone.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DesertPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.DESERT, Biome.DESERT_HILLS};

    public DesertPopulator() {
        super();
        waterLakeDecorator.setAmount(0);
        deadBushDecorator.setAmount(2);
        sugarCaneDecorator.setAmount(60);
        cactusDecorator.setAmount(10);
        desertWellDecorator.setAmount(1);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
