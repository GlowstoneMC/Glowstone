package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.block.Biome;

public class MesaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.MESA, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_MOUNTAINS, Biome.MESA_BRYCE};

    public MesaPopulator() {
        super();
        flowerDecorator.setAmount(0);
        deadBushDecorator.setAmount(20);
        sugarCaneDecorator.setAmount(13);
        cactusDecorator.setAmount(5);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
