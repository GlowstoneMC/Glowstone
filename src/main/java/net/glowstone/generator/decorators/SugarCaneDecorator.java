package net.glowstone.generator.decorators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SugarCaneDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);
        for (int j = 0; j < 20; j++) {
            int x = sourceX + random.nextInt(4) - random.nextInt(4);
            int z = sourceZ + random.nextInt(4) - random.nextInt(4);
            if (world.getBlockAt(x, sourceY, z).getType() == Material.AIR
            // needs a directly adjacent stationary water block
                    && (world.getBlockAt(x - 1, sourceY - 1, z).getType() == Material.STATIONARY_WATER
                            || world.getBlockAt(x + 1, sourceY - 1, z).getType() == Material.STATIONARY_WATER
                            || world.getBlockAt(x, sourceY - 1, z - 1).getType() == Material.STATIONARY_WATER
                            || world.getBlockAt(x, sourceY - 1, z + 1).getType() == Material.STATIONARY_WATER
                            // or a directly adjacent flowing water block
                            || world.getBlockAt(x - 1, sourceY - 1, z).getType() == Material.WATER
                            || world.getBlockAt(x + 1, sourceY - 1, z).getType() == Material.WATER
                            || world.getBlockAt(x, sourceY - 1, z - 1).getType() == Material.WATER || world
                            .getBlockAt(x, sourceY - 1, z + 1).getType() == Material.WATER)) {
                for (int n = 0; n <= random.nextInt(random.nextInt(3) + 1) + 1; n++) {
                    final Material type = world.getBlockAt(x, sourceY - 1, z).getType();
                    if (type == Material.SUGAR_CANE_BLOCK
                            || type == Material.GRASS
                            || (type == Material.DIRT && world.getBlockAt(x, sourceY - 1, z).getData() == 0)
                            || type == Material.SAND) {
                        final Block block = world.getBlockAt(x, sourceY + n, z);
                        block.setType(Material.SUGAR_CANE_BLOCK);
                        block.setData((byte) 0);
                    }
                }
            }
        }
    }
}
