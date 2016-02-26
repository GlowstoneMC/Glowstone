package net.glowstone.generator.decorators.theend;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.ObsidianPillar;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Random;

public class ObsidianPillarDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        if (random.nextInt(5) == 0) {
            int x = (source.getX() << 4) + random.nextInt(16);
            int z = (source.getZ() << 4) + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            new ObsidianPillar().generate(world, random, x, y, z);
        }
    }
}
