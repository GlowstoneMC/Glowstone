package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class RedwoodTree extends GenericTree {

    protected int maxRadius;
    protected int leavesHeight;

    public RedwoodTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setOverridables(
                Material.AIR,
                Material.LEAVES
        );
        setHeight(random.nextInt(4) + 6);
        setLeavesHeight(random.nextInt(2) + 1);
        setMaxRadius(random.nextInt(2) + 2);
        setTypes(1, 1);
    }

    protected final void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    protected final void setLeavesHeight(int leavesHeight) {
        this.leavesHeight = leavesHeight;
    }

    @Override
    public boolean canPlace() {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement
            int radius; // default radius if above first block
            if (y - loc.getBlockY() < leavesHeight) {
                radius = 0; // radius is 0 for trunk below leaves
            } else {
                radius = maxRadius;
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

    @Override
    public boolean generate() {
        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generate the leaves
        int radius = random.nextInt(2);
        int peakRadius = 1;
        int minRadius = 0;
        for (int y = loc.getBlockY() + height; y >= loc.getBlockY() + leavesHeight; y--) {
            // leaves are built from top to bottom
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if ((Math.abs(x - loc.getBlockX()) != radius || Math.abs(z - loc.getBlockZ()) != radius || radius <= 0) &&
                            delegate.getBlockState(loc.getWorld(), x, y, z).getType() == Material.AIR) {
                        delegate.setTypeAndRawData(loc.getWorld(), x, y, z, Material.LEAVES, leavesType);
                    }
                }
            }
            if (radius >= peakRadius) {
                radius = minRadius;
                minRadius = 1; // after the peak radius is reached once, the min radius increases
                peakRadius++;  // the peak radius increases each time it's reached
                if (peakRadius > maxRadius) {
                    peakRadius = maxRadius;
                }
            } else {
                radius++;
            }
        }

        // generate the trunk
        for (int y = 0; y < height - random.nextInt(3); y++) {
            final Material type = delegate.getBlockState(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ()).getType();
            if (overridables.contains(type)) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType);
            }
        }

        // block below trunk is always dirt
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);

        return true;
    }
}
