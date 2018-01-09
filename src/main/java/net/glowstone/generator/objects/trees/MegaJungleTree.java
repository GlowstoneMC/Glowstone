package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.Vine;
import org.bukkit.material.types.DirtType;

public class MegaJungleTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     *         leaf blocks
     */
    public MegaJungleTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(20) + random.nextInt(3) + 10);
        setTypes(3, 3);
    }

    @Override
    public boolean canPlaceOn() {
        BlockState state = delegate
                .getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT;
    }

    @Override
    public boolean canPlace() {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement
            int radius = 2; // default radius if above first block
            if (y == loc.getBlockY()) {
                radius = 1; // radius at source block y is 1 (only trunk)
            } else if (y >= loc.getBlockY() + 1 + height - 2) {
                radius = 2; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        Material type = blockTypeAt(x, y, z);
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

        // generates the canopy leaves
        for (int y = -2; y <= 0; y++) {
            generateLeaves(
                    loc.getBlockX() + 0, loc.getBlockY() + height + y, loc.getBlockZ(), 3 - y,
                    false);
        }

        // generates the branches
        int branchHeight = height - 2 - random.nextInt(4);
        while (branchHeight > height / 2) { // branching start at least at middle height
            int x = 0;
            int z = 0;
            // generates a branch
            float d = (float) (random.nextFloat() * Math.PI * 2.0F); // random direction
            for (int i = 0; i < 5; i++) {
                // branches are always longer when facing south or east (positive X or positive Z)
                x = (int) (Math.cos(d) * i + 1.5F);
                z = (int) (Math.sin(d) * i + 1.5F);
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + x,
                        loc.getBlockY() + branchHeight - 3 + i / 2,
                        loc.getBlockZ() + z, Material.LOG,
                        logType);
            }
            // generates leaves for this branch
            for (int y = branchHeight - (random.nextInt(2) + 1); y <= branchHeight; y++) {
                generateLeaves(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z,
                        1 - (y - branchHeight), true);
            }
            branchHeight -= random.nextInt(4) + 2;
        }

        // generates the trunk
        generateTrunk();

        // add some vines on the trunk
        addVinesOnTrunk();

        // blocks below trunk are always dirt
        generateDirtBelowTrunk();

        return true;
    }

    protected void generateLeaves(int sourceX, int sourceY, int sourceZ, int radius, boolean odd) {
        int n = 1;
        if (odd) {
            n = 0;
        }
        for (int x = sourceX - radius; x <= sourceX + radius + n; x++) {
            int radiusX = x - sourceX;
            for (int z = sourceZ - radius; z <= sourceZ + radius + n; z++) {
                int radiusZ = z - sourceZ;

                int sqX = radiusX * radiusX;
                int sqZ = radiusZ * radiusZ;
                int sqR = radius * radius;
                int sqXb = (radiusX - n) * (radiusX - n);
                int sqZb = (radiusZ - n) * (radiusZ - n);

                if (sqX + sqZ <= sqR || sqXb + sqZb <= sqR || sqX + sqZb <= sqR
                        || sqXb + sqZ <= sqR) {
                    replaceIfAirOrLeaves(x, sourceY, z, Material.LEAVES, leavesType);
                }
            }
        }
    }

    protected void generateTrunk() {
        // SELF, SOUTH, EAST, SOUTH EAST
        for (int y = 0; y < height + -1; y++) {
            Material type = loc.getWorld()
                    .getBlockAt(loc.getBlockX() + 0, loc.getBlockY() + y, loc.getBlockZ() + 0)
                    .getType();
            if (type == Material.AIR || type == Material.LEAVES) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 0, loc.getBlockY() + y,
                        loc.getBlockZ(), Material.LOG, logType);
            }
            type = loc.getWorld()
                    .getBlockAt(loc.getBlockX() + 0, loc.getBlockY() + y, loc.getBlockZ() + 1)
                    .getType();
            if (type == Material.AIR || type == Material.LEAVES) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 0, loc.getBlockY() + y,
                        loc.getBlockZ() + 1, Material.LOG, logType);
            }
            type = loc.getWorld()
                    .getBlockAt(loc.getBlockX() + 1, loc.getBlockY() + y, loc.getBlockZ() + 0)
                    .getType();
            if (type == Material.AIR || type == Material.LEAVES) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() + y,
                        loc.getBlockZ(), Material.LOG, logType);
            }
            type = loc.getWorld()
                    .getBlockAt(loc.getBlockX() + 1, loc.getBlockY() + y, loc.getBlockZ() + 1)
                    .getType();
            if (type == Material.AIR || type == Material.LEAVES) {
                delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() + y,
                        loc.getBlockZ() + 1, Material.LOG, logType);
            }
        }
    }

    protected void generateDirtBelowTrunk() {
        // SELF, SOUTH, EAST, SOUTH EAST
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
                .setTypeAndData(loc.getWorld(), loc.getBlockX() + 0,
                        loc.getBlockY() + -1, loc.getBlockZ(),
                        Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 0, loc.getBlockY() + -1,
                loc.getBlockZ() + 1, Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() + -1,
                loc.getBlockZ(), Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() + -1,
                loc.getBlockZ() + 1, Material.DIRT, dirt);
    }

    private void addVinesOnTrunk() {
        for (int y = 1; y < height; y++) {
            maybePlaceVine(-1, y, 0, BlockFace.EAST);
            maybePlaceVine(0, y, -1, BlockFace.SOUTH);
            maybePlaceVine(2, y, 0, BlockFace.WEST);
            maybePlaceVine(1, y, -1, BlockFace.SOUTH);
            maybePlaceVine(2, y, 1, BlockFace.WEST);
            maybePlaceVine(1, y, 2, BlockFace.NORTH);
            maybePlaceVine(-1, y, 1, BlockFace.EAST);
            maybePlaceVine(0, y, 2, BlockFace.NORTH);
        }
    }

    private void maybePlaceVine(int x, int y, int z, BlockFace east) {
        int absoluteX = loc.getBlockX() + x;
        int absoluteY = loc.getBlockY() + y;
        int absoluteZ = loc.getBlockZ() + z;
        if (random.nextInt(3) != 0
                && blockTypeAt(absoluteX, absoluteY, absoluteZ) == Material.AIR) {
            delegate.setTypeAndData(loc.getWorld(), absoluteX, absoluteY,
                    absoluteZ, Material.VINE, new Vine(east));
        }
    }

}
