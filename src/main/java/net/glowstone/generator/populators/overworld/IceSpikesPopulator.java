package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.IceDecorator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class IceSpikesPopulator extends SnowyTundraPopulator {

    protected final IceDecorator iceDecorator = new IceDecorator();

    public IceSpikesPopulator() {
        tallGrassDecorator.setAmount(0);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.ICE_SPIKES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        iceDecorator.populate(world, random, chunk);
        super.populateOnGround(world, random, chunk);
    }
}
