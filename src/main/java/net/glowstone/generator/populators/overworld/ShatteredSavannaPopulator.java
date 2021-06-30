package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class ShatteredSavannaPopulator extends SavannaPopulator {

    private static final Biome[] BIOMES = {Biome.SHATTERED_SAVANNA, Biome.SHATTERED_SAVANNA_PLATEAU};

    /**
     * Creates a populator specialized for the Shattered Savanna and Shattered Savanna Plateau biomes.
     */
    public ShatteredSavannaPopulator() {
        treeDecorator.setAmount(2);
        flowerDecorator.setAmount(2);
        tallGrassDecorator.setAmount(5);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
