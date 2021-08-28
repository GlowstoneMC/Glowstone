package net.glowstone.generator.populators;

import net.glowstone.generator.decorators.theend.ObsidianPillarDecorator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class TheEndPopulator extends BlockPopulator {

    private final ObsidianPillarDecorator obsidianPillarDecorator = new ObsidianPillarDecorator();

    public TheEndPopulator() {
        obsidianPillarDecorator.setAmount(1);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        obsidianPillarDecorator.populate(world, random, chunk);

        // spawn the enderdragon
        if (chunk.getX() == 0 && chunk.getZ() == 0) {
            final Location loc = new Location(world, (chunk.getX() << 4) + 8, 128,
                (chunk.getZ() << 4) + 8, random.nextFloat() * 360, 0);
            world.spawnEntity(loc, EntityType.ENDER_DRAGON);
        }
    }
}
