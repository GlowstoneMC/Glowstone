package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class TallRedwoodTree extends RedwoodTree {

    /**
     * Initializes this tree with a random height and radius, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public TallRedwoodTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
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
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generate the leaves
        int radius = 0;
        for (int y = blockY + height; y >= blockY + leavesHeight; y--) {
            // leaves are built from top to bottom
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    if ((Math.abs(x - blockX) != radius
                                    || Math.abs(z - blockZ) != radius || radius <= 0)
                            && blockTypeAt(x, y, z, world) == Material.AIR) {
                        delegate.setTypeAndRawData(world, x, y, z, Material.LEAVES,
                            leavesType);
                    }
                }
            }
            if (radius >= 1 && y == blockY + leavesHeight + 1) {
                radius--;
            } else if (radius < maxRadius) {
                radius++;
            }
        }

        // generate the trunk
        for (int y = 0; y < height - 1; y++) {
            replaceIfAirOrLeaves(blockX,
                    blockY + y, blockZ, Material.LOG, logType, world);
        }

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(world, blockX, blockY - 1, blockZ,
                Material.DIRT, dirt);

        return true;
    }
}
