package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

public class MegaJungleTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public MegaJungleTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(20) + random.nextInt(3) + 10);
        setTypes(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS || soil.getType() == Material.DIRT;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
            // Space requirement
            int radius = 2; // default radius if above first block
            if (y == baseY) {
                radius = 1; // radius at source block y is 1 (only trunk)
            } else if (y >= baseY + 1 + height - 2) {
                radius = 2; // max radius starting at leaves bottom
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

        // generates the canopy leaves
        for (int y = -2; y <= 0; y++) {
            generateLeaves(
                    blockX + 0, blockY + height + y, blockZ, 3 - y,
                    false, world);
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
                delegate.setType(world, blockX + x,
                        blockY + branchHeight - 3 + i / 2,
                        blockZ + z, logType);
            }
            // generates leaves for this branch
            for (int y = branchHeight - (random.nextInt(2) + 1); y <= branchHeight; y++) {
                generateLeaves(
                        blockX + x, blockY + y, blockZ + z,
                        1 - (y - branchHeight), true, world);
            }
            branchHeight -= random.nextInt(4) + 2;
        }

        // generates the trunk
        generateTrunk(world, blockX, blockY, blockZ);

        // add some vines on the trunk
        addVinesOnTrunk(world, blockX, blockY, blockZ, random);

        // blocks below trunk are always dirt
        generateDirtBelowTrunk(world, blockX, blockY, blockZ);

        return true;
    }

    protected void generateLeaves(int sourceX, int sourceY, int sourceZ, int radius, boolean odd,
                                  World world) {
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
                    replaceIfAirOrLeaves(x, sourceY, z, leavesType, world);
                }
            }
        }
    }

    protected void generateTrunk(World world, int blockX, int blockY, int blockZ) {
        // SELF, SOUTH, EAST, SOUTH EAST
        for (int y = 0; y < height + -1; y++) {
            Material type = world
                    .getBlockAt(blockX + 0, blockY + y, blockZ + 0)
                    .getType();
            if (type == Material.AIR || LEAF_TYPES.contains(type)) {
                delegate.setType(world, blockX + 0, blockY + y,
                        blockZ, logType);
            }
            type = world
                    .getBlockAt(blockX + 0, blockY + y, blockZ + 1)
                    .getType();
            if (type == Material.AIR || LEAF_TYPES.contains(type)) {
                delegate.setType(world, blockX + 0, blockY + y,
                        blockZ + 1, logType);
            }
            type = world
                    .getBlockAt(blockX + 1, blockY + y, blockZ + 0)
                    .getType();
            if (type == Material.AIR || LEAF_TYPES.contains(type)) {
                delegate.setType(world, blockX + 1, blockY + y,
                        blockZ, logType);
            }
            type = world
                    .getBlockAt(blockX + 1, blockY + y, blockZ + 1)
                    .getType();
            if (type == Material.AIR || LEAF_TYPES.contains(type)) {
                delegate.setType(world, blockX + 1, blockY + y,
                        blockZ + 1, logType);
            }
        }
    }

    protected void generateDirtBelowTrunk(World world, int blockX, int blockY, int blockZ) {
        // SELF, SOUTH, EAST, SOUTH EAST
        delegate.setType(world, blockX + 0, blockY + -1, blockZ + 0, Material.DIRT);
        delegate.setType(world, blockX + 0, blockY + -1, blockZ + 1, Material.DIRT);
        delegate.setType(world, blockX + 1, blockY + -1, blockZ + 0, Material.DIRT);
        delegate.setType(world, blockX + 1, blockY + -1, blockZ + 1, Material.DIRT);
    }

    private void addVinesOnTrunk(World world, int blockX, int blockY, int blockZ, Random random) {
        for (int y = 1; y < height; y++) {
            maybePlaceVine(world,
                    blockX + -1, blockY + y, blockZ + 0, BlockFace.EAST, random
            );
            maybePlaceVine(world,
                    blockX + 0, blockY + y, blockZ + -1, BlockFace.SOUTH, random
            );
            maybePlaceVine(world,
                    blockX + 2, blockY + y, blockZ + 0, BlockFace.WEST, random
            );
            maybePlaceVine(world,
                    blockX + 1, blockY + y, blockZ + -1, BlockFace.SOUTH, random
            );
            maybePlaceVine(world,
                    blockX + 2, blockY + y, blockZ + 1, BlockFace.WEST, random
            );
            maybePlaceVine(world,
                    blockX + 1, blockY + y, blockZ + 2, BlockFace.NORTH, random
            );
            maybePlaceVine(world,
                    blockX + -1, blockY + y, blockZ + 1, BlockFace.EAST, random
            );
            maybePlaceVine(world,
                    blockX + 0, blockY + y, blockZ + 2, BlockFace.NORTH, random
            );
        }
    }

    private void maybePlaceVine(World world, int absoluteX, int absoluteY,
                                int absoluteZ, BlockFace facingDirection, Random random) {
        if (random.nextInt(3) != 0
                && blockTypeAt(absoluteX, absoluteY, absoluteZ, world)
                == Material.AIR) {
            // TODO: 1.13, set direction of vine
            delegate.setType(world, absoluteX, absoluteY,
                    absoluteZ, Material.VINE);
        }
    }

}
