package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

public class SugarCane implements TerrainObject {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (!world.getBlockAt(x, y, z).isEmpty()) {
            return false;
        }
        Block block = world.getBlockAt(x, y, z).getRelative(BlockFace.DOWN);
        boolean adjacentWater = false;
        for (BlockFace face : FACES) {
            // needs a directly adjacent water block
            Material blockType = block.getRelative(face).getType();
            if (blockType == Material.WATER) {
                adjacentWater = true;
                break;
            }
        }
        if (!adjacentWater) {
            return false;
        }
        for (int n = 0; n <= random.nextInt(random.nextInt(3) + 1) + 1; n++) {
            block = world.getBlockAt(x, y + n, z).getRelative(BlockFace.DOWN);
            if (block.getType() == Material.SUGAR_CANE
                    || block.getType() == Material.GRASS_BLOCK
                    || block.getType() == Material.DIRT
                    || block.getType() == Material.SAND) {
                Block caneBlock = block.getRelative(BlockFace.UP);
                if (!caneBlock.isEmpty() && !caneBlock.getRelative(BlockFace.UP)
                        .isEmpty()) {
                    return n > 0;
                }
                BlockState state = caneBlock.getState();
                state.setType(Material.SUGAR_CANE);
                state.update(true);
            }
        }
        return true;
    }
}
