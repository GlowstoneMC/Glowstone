package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class JungleBush extends GenericTree {

    public JungleBush(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setTypes(3, 0);
    }

    @Override
    public boolean canPlaceOn() {
        final BlockState state = delegate.getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT;
    }

    @Override
    public boolean generate() {
        Location l = loc.clone();
        while ((l.getBlock().getType() == Material.AIR || l.getBlock().getType() == Material.LEAVES) && l.getBlockY() > 0) {
            l.subtract(0, 1, 0);
        }

        // check only below block
        if (!canPlaceOn()) {
            return false;
        }

        // generates the trunk
        delegate.setTypeAndRawData(l.getWorld(), l.getBlockX(), l.getBlockY() + 1, l.getBlockZ(), Material.LOG, logType);

        // generates the leaves
        for (int y = l.getBlockY() + 1; y <= l.getBlockY() + 3; y++) {
            int radius = 3 - (y - l.getBlockY());

            for (int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
                for (int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
                    if ((Math.abs(x - l.getBlockX()) != radius || Math.abs(z - l.getBlockZ()) != radius || random.nextBoolean()) &&
                            !delegate.getBlockState(l.getWorld(), x, y, z).getType().isSolid()) {
                        delegate.setTypeAndRawData(l.getWorld(), x, y, z, Material.LEAVES, leavesType);
                    }
                }
            }
        }

        return true;
    }
}
