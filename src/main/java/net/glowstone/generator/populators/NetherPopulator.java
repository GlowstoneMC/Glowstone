package net.glowstone.generator.populators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.glowstone.generator.populators.nether.OrePopulator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class NetherPopulator extends BlockPopulator {

    private final List<BlockPopulator> inGroundPopulators = new ArrayList<>();
    private final List<BlockPopulator> onGroundPopulators = new ArrayList<>();

    protected final OrePopulator orePopulator = new OrePopulator();

    public NetherPopulator() {
        inGroundPopulators.add(orePopulator);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        populateInGround(world, random, chunk);
        populateOnGround(world, random, chunk);
    }

    protected void populateInGround(World world, Random random, Chunk chunk) {
        for (BlockPopulator populator : inGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }

    protected void populateOnGround(World world, Random random, Chunk chunk) {
        for (BlockPopulator populator : onGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }
}
