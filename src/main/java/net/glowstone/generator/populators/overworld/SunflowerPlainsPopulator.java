package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import org.bukkit.block.Biome;
import org.bukkit.material.types.DoublePlantSpecies;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SunflowerPlainsPopulator extends PlainsPopulator {

    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {
        new DoublePlantDecoration(DoublePlantSpecies.SUNFLOWER, 1)};

    public SunflowerPlainsPopulator() {
        doublePlantDecorator.setAmount(10);
        doublePlantDecorator.setDoublePlants(DOUBLE_PLANTS);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.MUTATED_PLAINS));
    }
}
