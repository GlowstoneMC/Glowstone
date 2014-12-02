package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class SwampTree extends CocoaTree {

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
        final BlockState state = delegate.getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
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
                        final Material type = delegate.getBlockState(loc.getWorld(), x, y, z).getType();
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
        while ((loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER || loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER)) {
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
                    if (Math.abs(x - loc.getBlockX()) != radius || Math.abs(z - loc.getBlockZ()) != radius
                            || (random.nextBoolean() && n != 0)) {
                        final Material material = delegate.getBlockState(loc.getWorld(), x, y, z).getType();
                        if (material == Material.AIR || material == Material.LEAVES) {
                            delegate.setTypeAndRawData(loc.getWorld(), x, y, z, Material.LEAVES, leavesType);
                        }
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            final Material material = delegate.getBlockState(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ()).getType();
            if (material == Material.AIR || material == Material.LEAVES ||
                    material == Material.WATER || material == Material.STATIONARY_WATER) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType);
            }
        }

        // add some vines on the leaves
        addVinesOnLeaves();

        // block below trunk is always dirt
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);

        return true;
    }
}
