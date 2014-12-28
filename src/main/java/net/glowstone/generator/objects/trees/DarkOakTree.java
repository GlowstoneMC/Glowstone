package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class DarkOakTree extends GenericTree {

    public DarkOakTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(2) + random.nextInt(3) + 6);
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
        int twistHeight = height - random.nextInt(4);
        int twistCount = random.nextInt(3);
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
                // SELF, SOUTH, EAST, SOUTH EAST
                delegate.setTypeAndRawData(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ, Material.LOG_2, 1);
                delegate.setTypeAndRawData(loc.getWorld(), centerX, loc.getBlockY() + y, centerZ + 1, Material.LOG_2, 1);
                delegate.setTypeAndRawData(loc.getWorld(), centerX + 1, loc.getBlockY() + y, centerZ, Material.LOG_2, 1);
                delegate.setTypeAndRawData(loc.getWorld(), centerX + 1, loc.getBlockY() + y, centerZ + 1, Material.LOG_2, 1);
            }
        }

        // generates leaves
        for (int x = -2; x <= 0; x++) {
            for (int z = -2; z <= 0; z++) {
                if ((x != -1 || z != -2) && (x > -2 || z > -1)) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z);
                    setLeaves(1 + centerX - x, trunkTopY + 1, centerZ + z);
                    setLeaves(centerX + x, trunkTopY + 1, 1 + centerZ - z);
                    setLeaves(1 + centerX - x, trunkTopY + 1, 1 + centerZ - z);
                }
                setLeaves(centerX + x, trunkTopY - 1, centerZ + z);
                setLeaves(1 + centerX - x, trunkTopY - 1, centerZ + z);
                setLeaves(centerX + x, trunkTopY - 1, 1 + centerZ - z);
                setLeaves(1 + centerX - x, trunkTopY - 1, 1 + centerZ - z);
            }
        }

        // finish leaves below the canopy
        for (int x = -3; x <= 4; x++) {
            for (int z = -3; z <= 4; z++) {
                if (Math.abs(x) < 3 || Math.abs(z) < 3) {
                    setLeaves(centerX + x, trunkTopY, centerZ + z);
                }
            }
        }

        // generates some trunk excrescences
        for (int x = -1; x <= 2; x++) {
            for (int z = -1; z <= 2; z++) {
                if ((x == -1 || z == -1 || x == 2 || z == 2) && random.nextInt(3) == 0) {
                    for (int y = 0; y < random.nextInt(3) + 2; y++) {
                        final Material material = delegate.getBlockState(loc.getWorld(), loc.getBlockX() + x, trunkTopY - y - 1, loc.getBlockZ() + z).getType();
                        if (material == Material.AIR || material == Material.LEAVES) {
                            delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + x, trunkTopY - y - 1, loc.getBlockZ() + z, Material.LOG_2, 1);
                        }
                    }

                    // leaves below the canopy
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            setLeaves(centerX + x + i, trunkTopY, centerZ + z + j);
                        }
                    }
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            if ((Math.abs(i) < 2) || (Math.abs(j) < 2)) {
                                setLeaves(centerX + x + i, trunkTopY - 1, centerZ + z + j);
                            }
                        }
                    }
                }
            }
        }

        // 50% chance to have a 4 leaves cap on the center of the canopy
        if (random.nextInt(2) == 0) {
            setLeaves(centerX, trunkTopY + 2, centerZ);
            setLeaves(centerX + 1, trunkTopY + 2, centerZ);
            setLeaves(centerX + 1, trunkTopY + 2, centerZ + 1);
            setLeaves(centerX, trunkTopY + 2, centerZ + 1);
        }

        // block below trunk is always dirt (SELF, SOUTH, EAST, SOUTH EAST)
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ() + 1, Material.DIRT, 0);
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1, loc.getBlockZ(), Material.DIRT, 0);
        delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1, loc.getBlockZ() + 1, Material.DIRT, 0);

        return true;
    }

    private void setLeaves(int x, int y, int z) {
        if (delegate.getBlockState(loc.getWorld(), x, y, z).getType() == Material.AIR) {
            delegate.setTypeAndRawData(loc.getWorld(), x, y, z, Material.LEAVES_2, 1);
        }
    }
}
