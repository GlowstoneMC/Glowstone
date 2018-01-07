package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class TallRedwoodTree extends RedwoodTree {

    /**
     * Initializes this tree with a random height and radius, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     *         leaf blocks
     */
    public TallRedwoodTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
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
        setHeight(random.nextInt(5) + 7);
        setLeavesHeight(height - random.nextInt(2) - 3);
        setMaxRadius(random.nextInt(height - leavesHeight + 1) + 1);
    }

    @Override
    public boolean generate() {
        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generate the leaves
        int radius = 0;
        for (int y = loc.getBlockY() + height; y >= loc.getBlockY() + leavesHeight; y--) {
            // leaves are built from top to bottom
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if ((Math.abs(x - loc.getBlockX()) != radius
                                    || Math.abs(z - loc.getBlockZ()) != radius || radius <= 0)
                            && blockTypeAt(x, y, z) == Material.AIR) {
                        delegate.setTypeAndRawData(loc.getWorld(), x, y, z, Material.LEAVES,
                            leavesType);
                    }
                }
            }
            if (radius >= 1 && y == loc.getBlockY() + leavesHeight + 1) {
                radius--;
            } else if (radius < maxRadius) {
                radius++;
            }
        }

        // generate the trunk
        for (int y = 0; y < height - 1; y++) {
            replaceIfAirOrLeaves(loc.getBlockX(),
                    loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType);
        }

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(),
                Material.DIRT, dirt);

        return true;
    }
}
