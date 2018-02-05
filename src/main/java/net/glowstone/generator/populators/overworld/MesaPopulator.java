package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class MesaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.MESA, Biome.MESA_CLEAR_ROCK,
        Biome.MUTATED_MESA_CLEAR_ROCK, Biome.MUTATED_MESA};

    /**
     * Creates a populator specialized for mesa biomes.
     */
    public MesaPopulator() {
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
