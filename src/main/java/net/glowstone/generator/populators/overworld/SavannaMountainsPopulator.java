package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class SavannaMountainsPopulator extends SavannaPopulator {

    private static final Biome[] BIOMES = {Biome.MUTATED_SAVANNA, Biome.MUTATED_SAVANNA_ROCK};

    /**
     * Creates a populator specialized for the Savanna M and Savanna Plateau M biomes.
     */
    public SavannaMountainsPopulator() {
        treeDecorator.setAmount(2);
        flowerDecorator.setAmount(2);
        tallGrassDecorator.setAmount(5);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
