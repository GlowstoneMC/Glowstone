package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.objects.StoneBoulder;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class StoneBoulderDecorator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;
        for (int i = 0; i < random.nextInt(3); i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            new StoneBoulder().generate(world, random, x, y, z);
        }
    }
}
