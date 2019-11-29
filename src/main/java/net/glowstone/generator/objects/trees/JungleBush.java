package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

public class JungleBush extends GenericTree {

    /**
     * Initializes this bush, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public JungleBush(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        // In Java Edition, the bushes have jungle logs and oak leaves
        setTypes(Material.JUNGLE_LOG, Material.OAK_LEAVES);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS_BLOCK || soil.getType() == Material.DIRT;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        Location l = new Location(world, blockX, blockY, blockZ);
        while ((l.getBlock().getType() == Material.AIR || LEAF_TYPES.contains(l.getBlock().getType()))
                && blockY > 0) {
            l.subtract(0, 1, 0);
        }

        // check only below block
        if (!canPlaceOn(l.getBlock().getRelative(BlockFace.DOWN).getState())) {
            return false;
        }

        // generates the trunk
        final int adjustedY = l.getBlockY();
        delegate.setType(world, blockX, adjustedY + 1, blockZ,
                logType);

        // generates the leaves
        for (int y = adjustedY + 1; y <= adjustedY + 3; y++) {
            int radius = 3 - (y - adjustedY);

            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    if ((Math.abs(x - l.getBlockX()) != radius
                            || Math.abs(z - l.getBlockZ()) != radius || random.nextBoolean())
                            && !delegate.getBlockState(world, x, y, z).getType().isSolid()) {
                        delegate.setType(world, x, y, z, leavesType);
                    }
                }
            }
        }

        return true;
    }
}
