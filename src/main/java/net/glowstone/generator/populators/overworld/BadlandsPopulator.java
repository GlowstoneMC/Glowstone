package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class BadlandsPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.BADLANDS, Biome.BADLANDS_PLATEAU,
        Biome.MODIFIED_BADLANDS_PLATEAU, Biome.ERODED_BADLANDS};

    /**
     * Creates a populator specialized for badlands biomes.
     */
    public BadlandsPopulator() {
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
