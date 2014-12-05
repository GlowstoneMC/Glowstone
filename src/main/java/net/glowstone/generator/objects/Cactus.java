package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Cactus {

    private BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public void generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockAt(x, y, z).isEmpty()) {
            int height = random.nextInt(random.nextInt(3) + 1) + 1;
            for (int n = y; n < y + height; n++) {
                final Block block = world.getBlockAt(x, n, z);
                if ((block.getRelative(BlockFace.DOWN).getType() == Material.SAND ||
                        block.getRelative(BlockFace.DOWN).getType() == Material.CACTUS) &&
                        block.getRelative(BlockFace.UP).isEmpty()) {
                    for (BlockFace face : faces) {
                        if (block.getRelative(face).getType().isSolid()) {
                            return;
                        }
                    }
                    block.setType(Material.CACTUS);
                    block.setData((byte) 0);
                }
            }
        }
    }
}
