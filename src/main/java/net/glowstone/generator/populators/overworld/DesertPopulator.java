package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.EntityDecorator;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DesertPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.DESERT, Biome.DESERT_HILLS};

    /**
     * Creates a {@link BiomePopulator} adjusted for the desert.
     */
    public DesertPopulator() {
        waterLakeDecorator.setAmount(0);
        deadBushDecorator.setAmount(2);
        sugarCaneDecorator.setAmount(60);
        cactusDecorator.setAmount(10);
        desertWellDecorator.setAmount(1);
        entityDecorators.clear();
        EntityDecorator rabbitDecorator = new EntityDecorator(EntityType.RABBIT);
        rabbitDecorator.setGroupSize(2, 3);
        entityDecorators.add(rabbitDecorator);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
