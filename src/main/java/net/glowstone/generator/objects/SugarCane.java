package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SugarCane {

    private BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public void generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockAt(x, y, z).isEmpty()) {
            Block block = world.getBlockAt(x, y, z).getRelative(BlockFace.DOWN);
            boolean adjacentWater = false;
            for (BlockFace face : faces) {
                // needs a directly adjacent water block
                if (block.getRelative(face).getType() == Material.STATIONARY_WATER ||
                        block.getRelative(face).getType() == Material.WATER) {
                    adjacentWater = true;
                    break;
                }
            }
            if (adjacentWater) {
                for (int n = 0; n <= random.nextInt(random.nextInt(3) + 1) + 1; n++) {
                    block = world.getBlockAt(x, y + n, z).getRelative(BlockFace.DOWN);
                    if (block.getType() == Material.SUGAR_CANE_BLOCK
                            || block.getType() == Material.GRASS
                            || (block.getType() == Material.DIRT && block.getData() == 0)
                            || block.getType() == Material.SAND) {
                        final Block caneBlock = block.getRelative(BlockFace.UP);
                        if (!caneBlock.isEmpty() && !caneBlock.getRelative(BlockFace.UP).isEmpty()) {
                            return;
                        }
                        caneBlock.setType(Material.SUGAR_CANE_BLOCK);
                        caneBlock.setData((byte) 0);
                    }
                }
            }
        }
    }
}
