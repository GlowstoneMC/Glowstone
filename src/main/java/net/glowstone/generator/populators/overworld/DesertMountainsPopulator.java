package net.glowstone.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Collection;
import java.util.Collections;

public class DesertMountainsPopulator extends DesertPopulator {

    public DesertMountainsPopulator() {
        waterLakeDecorator.setAmount(1);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.emptyList();
    }
}
