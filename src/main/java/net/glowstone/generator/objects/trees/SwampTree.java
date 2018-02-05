package net.glowstone.generator.objects.trees;

import com.google.common.collect.ImmutableList;
import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class SwampTree extends CocoaTree {

    public static final ImmutableList<Material> WATER_BLOCK_TYPES
            = ImmutableList.of(Material.WATER, Material.STATIONARY_WATER);

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public SwampTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setOverridables(
            Material.AIR,
            Material.LEAVES
        );
        setHeight(random.nextInt(4) + 5);
        setTypes(0, 0);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS || soil.getType() == Material.DIRT;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
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
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        Material type = blockTypeAt(x, y, z, world);
                        if (!overridables.contains(type)) {
                            // the trunk can be immersed by 1 block of water
                            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                                if (y > baseY) {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    } else { // height out of range
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        while (WATER_BLOCK_TYPES.contains(world.getBlockAt(blockX, blockY, blockZ).getType())) {
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
                        replaceIfAirOrLeaves(x, y, z, Material.LEAVES, leavesType, world);
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            Material material = blockTypeAt(blockX, blockY + y, blockZ, world);
            if (material == Material.AIR || material == Material.LEAVES
                    || material == Material.WATER || material == Material.STATIONARY_WATER) {
                delegate.setTypeAndRawData(
                        world, blockX, blockY + y, blockZ, Material.LOG, logType);
            }
        }

        // add some vines on the leaves
        addVinesOnLeaves(blockX, blockY, blockZ, world, random);

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(world, blockX, blockY - 1, blockZ,
                Material.DIRT, dirt);

        return true;
    }
}
