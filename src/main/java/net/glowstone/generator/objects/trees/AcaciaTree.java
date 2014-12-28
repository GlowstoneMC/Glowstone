package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class AcaciaTree extends GenericTree {

    public AcaciaTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(3) + random.nextInt(3) + 5);
    }

    @Override
    public boolean canPlaceOn() {
        final BlockState state = delegate.getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT;
    }

    @Override
    public boolean generate() {

        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        float d = (float) (random.nextFloat() * Math.PI * 2.0F); // random direction
        int dx = ((int) (Math.cos(d) + 1.5F)) - 1;
        int dz = ((int) (Math.sin(d) + 1.5F)) - 1;
        if (Math.abs(dx) > 0 && Math.abs(dz) > 0) { // reduce possible directions to NSEW
            if (random.nextBoolean()) {
                dx = 0;
            } else {
                dz = 0;
            }
        }
        int twistHeight = height - 1 - random.nextInt(4);
        int twistCount = random.nextInt(3) + 1;
        int centerX = loc.getBlockX(), centerZ = loc.getBlockZ();
        int trunkTopY = 0;

        // generates the trunk
        for (int y = 0; y < height; y++) {

            // trunk twists
            if (twistCount > 0 && y >= twistHeight) {
                centerX += dx;
                centerZ += dz;
                twistCount--;
            }

            final Material material = delegate.getBlockState(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ).getType();
            if (material == Material.AIR || material == Material.LEAVES) {
                trunkTopY = loc.getBlockY() + y;
                delegate.setTypeAndRawData(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ, Material.LOG_2, 0);
            }
        }

        // generates leaves
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) < 3 || Math.abs(z) < 3) {
                    setLeaves(centerX + x, trunkTopY, centerZ + z);
                }
                if (Math.abs(x) < 2 && Math.abs(z) < 2) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z);
                }
                if ((Math.abs(x) == 2 && Math.abs(z) == 0) || (Math.abs(x) == 0 && Math.abs(z) == 2)) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z);
                }
            }
        }

        // try to choose a different direction for second branching and canopy
        d = (float) (random.nextFloat() * Math.PI * 2.0F);
        int dxB = ((int) (Math.cos(d) + 1.5F)) - 1;
        int dzB = ((int) (Math.sin(d) + 1.5F)) - 1;
        if (Math.abs(dxB) > 0 && Math.abs(dzB) > 0) {
            if (random.nextBoolean()) {
                dxB = 0;
            } else {
                dzB = 0;
            }
        }
        if (dx != dxB || dz != dzB) {
            centerX = loc.getBlockX();
            centerZ = loc.getBlockZ();
            int branchHeight = twistHeight - 1 - random.nextInt(2);
            twistCount = random.nextInt(3) + 1;
            trunkTopY = 0;

            // generates the trunk
            for (int y = branchHeight + 1; y < height; y++) {
                if (twistCount > 0) {
                    centerX += dxB;
                    centerZ += dzB;
                    final Material material = delegate.getBlockState(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ).getType();
                    if (material == Material.AIR || material == Material.LEAVES) {
                        trunkTopY = loc.getBlockY() + y;
                        delegate.setTypeAndRawData(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ, Material.LOG_2, 0);
                    }
                    twistCount--;
                }
            }

            // generates the leaves
            if (trunkTopY > 0) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if ((Math.abs(x) < 2) || (Math.abs(z) < 2)) {
                            setLeaves(centerX + x, trunkTopY, centerZ + z);
                        }
                    }
                }
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        setLeaves(centerX + x, trunkTopY + 1, centerZ + z);
                    }
                }
            }
        }

        // block below trunk is always dirt
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);

        return true;
    }

    private void setLeaves(int x, int y, int z) {
        if (delegate.getBlockState(loc.getWorld(), x, y, z).getType() == Material.AIR) {
            delegate.setTypeAndRawData(loc.getWorld(), x, y, z, Material.LEAVES_2, 0);
        }
    }
}
