package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.block.Biome;

public class DesertMountainsPopulator extends DesertPopulator {

    public DesertMountainsPopulator() {
        waterLakeDecorator.setAmount(1);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.MUTATED_DESERT));
    }
}
