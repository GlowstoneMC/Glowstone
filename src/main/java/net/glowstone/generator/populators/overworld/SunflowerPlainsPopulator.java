package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import org.bukkit.Material;
import org.bukkit.block.Biome;

public class SunflowerPlainsPopulator extends PlainsPopulator {

    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {
        new DoublePlantDecoration(Material.SUNFLOWER, 1)};

    public SunflowerPlainsPopulator() {
        doublePlantDecorator.setAmount(10);
        doublePlantDecorator.setDoublePlants(DOUBLE_PLANTS);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.SUNFLOWER_PLAINS));
    }
}
