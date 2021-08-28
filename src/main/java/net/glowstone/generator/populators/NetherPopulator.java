package net.glowstone.generator.populators;

import net.glowstone.generator.decorators.nether.FireDecorator;
import net.glowstone.generator.decorators.nether.GlowstoneDecorator;
import net.glowstone.generator.decorators.nether.LavaDecorator;
import net.glowstone.generator.decorators.nether.MushroomDecorator;
import net.glowstone.generator.populators.nether.OrePopulator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetherPopulator extends BlockPopulator {

    private final List<BlockPopulator> inGroundPopulators = new ArrayList<>();
    private final List<BlockPopulator> onGroundPopulators = new ArrayList<>();

    private final OrePopulator orePopulator = new OrePopulator();

    private final LavaDecorator flowingLavaDecorator = new LavaDecorator(true);
    private final FireDecorator fireDecorator = new FireDecorator();
    private final GlowstoneDecorator glowstoneDecorator1 = new GlowstoneDecorator(true);
    private final GlowstoneDecorator glowstoneDecorator2 = new GlowstoneDecorator();
    private final MushroomDecorator brownMushroomDecorator = new MushroomDecorator(
        Material.BROWN_MUSHROOM);
    private final MushroomDecorator redMushroomDecorator = new MushroomDecorator(
        Material.RED_MUSHROOM);
    private final LavaDecorator lavaDecorator = new LavaDecorator();

    /**
     * Creates a populator specialized for the Nether.
     */
    public NetherPopulator() {
        inGroundPopulators.add(orePopulator);

        onGroundPopulators.add(flowingLavaDecorator);
        onGroundPopulators.add(fireDecorator);
        onGroundPopulators.add(glowstoneDecorator1);
        onGroundPopulators.add(glowstoneDecorator2);
        onGroundPopulators.add(fireDecorator);
        onGroundPopulators.add(brownMushroomDecorator);
        onGroundPopulators.add(redMushroomDecorator);
        onGroundPopulators.add(lavaDecorator);

        flowingLavaDecorator.setAmount(8);
        fireDecorator.setAmount(1);
        glowstoneDecorator1.setAmount(1);
        glowstoneDecorator2.setAmount(1);
        brownMushroomDecorator.setAmount(1);
        redMushroomDecorator.setAmount(1);
        lavaDecorator.setAmount(16);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        populateInGround(world, random, chunk);
        populateOnGround(world, random, chunk);
    }

    private void populateInGround(World world, Random random, Chunk chunk) {
        for (BlockPopulator populator : inGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }

    private void populateOnGround(World world, Random random, Chunk chunk) {
        for (BlockPopulator populator : onGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }
}
