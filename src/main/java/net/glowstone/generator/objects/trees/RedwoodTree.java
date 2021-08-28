package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class RedwoodTree extends GenericTree {

    protected int maxRadius;
    protected int leavesHeight;

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public RedwoodTree(Random random, BlockStateDelegate delegate) {
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
        setHeight(random.nextInt(4) + 6);
        setLeavesHeight(random.nextInt(2) + 1);
        setMaxRadius(random.nextInt(2) + 2);
        setTypes(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES);
    }

    protected final void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    protected final void setLeavesHeight(int leavesHeight) {
        this.leavesHeight = leavesHeight;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
            // Space requirement
            int radius; // default radius if above first block
            if (y - baseY < leavesHeight) {
                radius = 0; // radius is 0 for trunk below leaves
            } else {
                radius = maxRadius;
            }
            // check for block collision on horizontal slices
            for (int x = baseX - radius; x <= baseX + radius; x++) {
                for (int z = baseZ - radius; z <= baseZ + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        Material type = blockTypeAt(x, y, z, world);
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
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generate the leaves
        int radius = random.nextInt(2);
        int peakRadius = 1;
        int minRadius = 0;
        for (int y = blockY + height; y >= blockY + leavesHeight; y--) {
            // leaves are built from top to bottom
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    if ((Math.abs(x - blockX) != radius
                            || Math.abs(z - blockZ) != radius || radius <= 0)
                            && blockTypeAt(x, y, z, world) == Material.AIR) {
                        delegate.setType(world, x, y, z, leavesType);
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
            Material type = blockTypeAt(blockX, blockY + y, blockZ, world);
            if (overridables.contains(type)) {
                delegate.setType(world, blockX, blockY + y,
                        blockZ, logType);
            }
        }

        // block below trunk is always dirt
        delegate.setType(world, blockX,
                blockY - 1, blockZ,
                Material.DIRT);

        return true;
    }
}
