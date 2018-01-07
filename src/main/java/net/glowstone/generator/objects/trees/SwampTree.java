package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class SwampTree extends CocoaTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     *         leaf blocks
     */
    public SwampTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setOverridables(
            Material.AIR,
            Material.LEAVES
        );
        setHeight(random.nextInt(4) + 5);
        setTypes(0, 0);
    }

    @Override
    public boolean canPlaceOn() {
        BlockState state = delegate
            .getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT;
    }

    @Override
    public boolean canPlace() {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == loc.getBlockY()) {
                radius = 0; // radius at source block y is 0 (only trunk)
            } else if (y >= loc.getBlockY() + 1 + height - 2) {
                radius = 3; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        Material type = blockTypeAt(x, y, z);
                        if (!overridables.contains(type)) {
                            // the trunk can be immersed by 1 block of water
                            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                                if (y > loc.getBlockY()) {
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
    public boolean generate() {
        while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER
            || loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER) {
            loc.subtract(0, 1, 0);
        }

        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generate the leaves
        for (int y = loc.getBlockY() + height - 3; y <= loc.getBlockY() + height; y++) {
            int n = y - (loc.getBlockY() + height);
            int radius = 2 - n / 2;
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (Math.abs(x - loc.getBlockX()) != radius
                        || Math.abs(z - loc.getBlockZ()) != radius
                        || random.nextBoolean() && n != 0) {
                        replaceIfAirOrLeaves(x, y, z, Material.LEAVES, leavesType);
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            Material material = blockTypeAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ());
            if (material == Material.AIR || material == Material.LEAVES
                    || material == Material.WATER || material == Material.STATIONARY_WATER) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y,
                    loc.getBlockZ(), Material.LOG, logType);
            }
        }

        // add some vines on the leaves
        addVinesOnLeaves();

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(),
                Material.DIRT, dirt);

        return true;
    }
}
