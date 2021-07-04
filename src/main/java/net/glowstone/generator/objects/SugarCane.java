package net.glowstone.generator.objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DirtType;

import java.util.Random;

public class SugarCane implements TerrainObject {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
        BlockFace.WEST};

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
            if (blockType == Material.STATIONARY_WATER || blockType == Material.WATER) {
                adjacentWater = true;
                break;
            }
        }
        if (!adjacentWater) {
            return false;
        }
        for (int n = 0; n <= random.nextInt(random.nextInt(3) + 1) + 1; n++) {
            block = world.getBlockAt(x, y + n, z).getRelative(BlockFace.DOWN);
            if (block.getType() == Material.SUGAR_CANE_BLOCK
                || block.getType() == Material.GRASS
                || block.getType() == Material.DIRT && block.getState()
                .getData() instanceof Dirt
                && ((Dirt) block.getState().getData()).getType() == DirtType.NORMAL
                || block.getType() == Material.SAND) {
                Block caneBlock = block.getRelative(BlockFace.UP);
                if (!caneBlock.isEmpty() && !caneBlock.getRelative(BlockFace.UP)
                    .isEmpty()) {
                    return n > 0;
                }
                BlockState state = caneBlock.getState();
                state.setType(Material.SUGAR_CANE_BLOCK);
                state.setData(new MaterialData(Material.SUGAR_CANE_BLOCK));
                state.update(true);
            }
        }
        return true;
    }
}
