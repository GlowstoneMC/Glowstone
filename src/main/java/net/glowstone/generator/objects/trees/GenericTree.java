package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class GenericTree {

    protected final Random random;
    protected final Location loc;
    protected int height;
    protected int logType;
    protected int leavesType;
    protected final BlockStateDelegate delegate;
    protected Collection<Material> overridables;

    public GenericTree(Random random, Location location, BlockStateDelegate delegate) {
        this.random = random;
        this.loc = location;
        this.delegate = delegate;
        setOverridables(
                Material.AIR,
                Material.LEAVES,
                Material.GRASS,
                Material.DIRT,
                Material.LOG,
                Material.LOG_2,
                Material.SAPLING,
                Material.VINE
        );
        setHeight(random.nextInt(3) + 4);
        setTypes(0, 0);
    }

    protected final void setOverridables(Material... overridables) {
        this.overridables = Arrays.asList(overridables);
    }

    protected final void setHeight(int height) {
        this.height = height;
    }

    protected final void setTypes(int logType, int leavesType) {
        this.logType = logType;
        this.leavesType = leavesType;
    }

    public boolean canHeightFit() {
        return loc.getBlockY() >= 1 && loc.getBlockY() + height + 1 <= 255;
    }

    public boolean canPlaceOn() {
        final BlockState state = delegate.getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT || state.getType() == Material.SOIL;
    }

    public boolean canPlace() {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == loc.getBlockY()) {
                radius = 0; // radius at source block y is 0 (only trunk)
            } else if (y >= loc.getBlockY() + 1 + height - 2) {
                radius = 2; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        final Material type = delegate.getBlockState(loc.getWorld(), x, y, z).getType();
                        if (!overridables.contains(type)) {
                            return false;
                        }
                    } else { // height out of range
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean generate() {
        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generate the leaves
        for (int y = loc.getBlockY() + height - 3; y <= loc.getBlockY() + height; y++) {
            int n = y - (loc.getBlockY() + height);
            int radius = 1 - n / 2;
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (Math.abs(x - loc.getBlockX()) != radius || Math.abs(z - loc.getBlockZ()) != radius || (random.nextBoolean() && n != 0)) {
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
            if (material == Material.AIR || material == Material.LEAVES) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType);
            }
        }

        // block below trunk is always dirt
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);

        return true;
    }
}
