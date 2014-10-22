package net.glowstone.generator.decorators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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

            if (world.getBlockAt(x, y, z).getType() == Material.AIR) {

                int height = random.nextInt(random.nextInt(3) + 1) + 1;
                for (int n = y; n < y + height; n++) {
                    final Block block = world.getBlockAt(x, n, z);
                    if ((world.getBlockAt(x, n - 1, z).getType() == Material.SAND ||
                            world.getBlockAt(x, n - 1, z).getType() == Material.CACTUS) &&
                            world.getBlockAt(x, n + 1, z).getType() == Material.AIR &&
                            !world.getBlockAt(x - 1, n, z).getType().isSolid() &&
                            !world.getBlockAt(x + 1, n, z).getType().isSolid() &&
                            !world.getBlockAt(x, n, z - 1).getType().isSolid() &&
                            !world.getBlockAt(x, n, z + 1).getType().isSolid())
                    block.setType(Material.CACTUS);
                    block.setData((byte) 0);
                }
            }
        }
    }
}
