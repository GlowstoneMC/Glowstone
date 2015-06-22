package net.glowstone.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SavannaMountainsPopulator extends SavannaPopulator {

    private static final Biome[] BIOMES = {Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU_MOUNTAINS};

    public SavannaMountainsPopulator() {
        super();
        treeDecorator.setAmount(2);
        flowerDecorator.setAmount(2);
        tallGrassDecorator.setAmount(5);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
