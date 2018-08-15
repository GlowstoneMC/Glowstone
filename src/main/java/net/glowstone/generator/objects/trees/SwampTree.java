package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public class SwampTree extends CocoaTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public SwampTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setOverridables(
                Material.AIR,
                // Leaves
                Material.OAK_LEAVES,
                Material.SPRUCE_LEAVES,
                Material.BIRCH_LEAVES,
                Material.JUNGLE_LEAVES,
                Material.ACACIA_LEAVES,
                Material.DARK_OAK_LEAVES
        );
        setHeight(random.nextInt(4) + 5);
        setTypes(Material.OAK_LOG, Material.OAK_LEAVES);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS || soil.getType() == Material.DIRT;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
            if (y < 0 || y >= 256) { // height out of range
                return false;
            }
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == baseY) {
                radius = 0; // radius at source block y is 0 (only trunk)
            } else if (y >= baseY + 1 + height - 2) {
                radius = 3; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = baseX - radius; x <= baseX + radius; x++) {
                for (int z = baseZ - radius; z <= baseZ + radius; z++) {
                    // we can overlap some blocks around
                    Material type = blockTypeAt(x, y, z, world);
                    if (overridables.contains(type)) {
                        continue;
                    }
                    // the trunk can be immersed by 1 block of water
                    if (type == Material.WATER) {
                        if (y > baseY) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        while (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.WATER) {
            blockY--;
        }

        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generate the leaves
        for (int y = blockY + height - 3; y <= blockY + height; y++) {
            int n = y - (blockY + height);
            int radius = 2 - n / 2;
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    if (Math.abs(x - blockX) != radius
                            || Math.abs(z - blockZ) != radius
                            || random.nextBoolean() && n != 0) {
                        replaceIfAirOrLeaves(x, y, z, leavesType, world);
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            Material material = blockTypeAt(blockX, blockY + y, blockZ, world);
            if (material == Material.AIR || LEAF_TYPES.contains(material)
                    || material == Material.WATER) {
                delegate.setType(world, blockX, blockY + y, blockZ, logType);
            }
        }

        // add some vines on the leaves
        addVinesOnLeaves(blockX, blockY, blockZ, world, random);

        // block below trunk is always dirt
        delegate.setType(world, blockX, blockY - 1, blockZ, Material.DIRT);

        return true;
    }
}
