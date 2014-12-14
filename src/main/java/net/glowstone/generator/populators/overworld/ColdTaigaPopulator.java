package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.glowstone.generator.decorators.overworld.SnowDecorator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class ColdTaigaPopulator extends TaigaPopulator {

    private static final Biome[] BIOMES = {Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_TAIGA_MOUNTAINS};

    protected final SnowDecorator snowDecorator = new SnowDecorator();

    public ColdTaigaPopulator() {
        super();
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        snowDecorator.populate(world, random, chunk);
    }
}
