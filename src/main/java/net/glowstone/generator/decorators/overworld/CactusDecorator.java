package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.Cactus;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Random;

public class CactusDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);

        for (int i = 0; i < 10; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);
            new Cactus().generate(world, random, x, y, z);
        }
    }
}
