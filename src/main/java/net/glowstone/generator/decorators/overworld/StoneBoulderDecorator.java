package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.StoneBoulder;

public class StoneBoulderDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = source.getX() << 4;
        int sourceZ = source.getZ() << 4;
        for (int i = 0; i < random.nextInt(3); i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            new StoneBoulder().generate(world, random, x, y, z);
        }
    }
}
