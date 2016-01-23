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

    public NetherPopulator() {
        OrePopulator orePopulator = new OrePopulator();
        inGroundPopulators.add(orePopulator);

        LavaDecorator flowingLavaDecorator = new LavaDecorator(true);
        onGroundPopulators.add(flowingLavaDecorator);
        FireDecorator fireDecorator = new FireDecorator();
        onGroundPopulators.add(fireDecorator);
        GlowstoneDecorator glowstoneDecorator1 = new GlowstoneDecorator(true);
        onGroundPopulators.add(glowstoneDecorator1);
        GlowstoneDecorator glowstoneDecorator2 = new GlowstoneDecorator();
        onGroundPopulators.add(glowstoneDecorator2);
        onGroundPopulators.add(fireDecorator);
        MushroomDecorator brownMushroomDecorator = new MushroomDecorator(Material.BROWN_MUSHROOM);
        onGroundPopulators.add(brownMushroomDecorator);
        MushroomDecorator redMushroomDecorator = new MushroomDecorator(Material.RED_MUSHROOM);
        onGroundPopulators.add(redMushroomDecorator);
        LavaDecorator lavaDecorator = new LavaDecorator();
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
