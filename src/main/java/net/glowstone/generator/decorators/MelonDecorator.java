package net.glowstone.generator.decorators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class MelonDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);

        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (world.getBlockAt(x, y, z).getType() == Material.AIR &&
                    world.getBlockAt(x, y - 1, z).getType() == Material.GRASS) {
                final Block block = world.getBlockAt(x, y, z);
                block.setType(Material.MELON_BLOCK);
                block.setData((byte) 0);
            }
        }
    }
}
