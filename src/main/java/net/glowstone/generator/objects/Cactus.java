package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class Cactus {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
        BlockFace.WEST};

    /**
     * Generates or extends a cactus, if there is space.
     * @param world the world
     * @param random the PRNG that will determine the height
     * @param x the x coordinate
     * @param y the y coordinate of the bottom new block
     * @param z the z coordinate
     */
    public void generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockAt(x, y, z).isEmpty()) {
            int height = random.nextInt(random.nextInt(3) + 1) + 1;
            for (int n = y; n < y + height; n++) {
                Block block = world.getBlockAt(x, n, z);
                Material typeBelow = block.getRelative(BlockFace.DOWN).getType();
                if ((typeBelow == Material.SAND || typeBelow == Material.CACTUS)
                        && block.getRelative(BlockFace.UP).isEmpty()) {
                    for (BlockFace face : FACES) {
                        if (block.getRelative(face).getType().isSolid()) {
                            return;
                        }
                    }
                    BlockState state = block.getState();
                    state.setType(Material.CACTUS);
                    state.setData(new MaterialData(Material.CACTUS));
                    state.update(true);
                }
            }
        }
    }
}
