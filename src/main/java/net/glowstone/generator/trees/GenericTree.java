package net.glowstone.generator.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Vine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class GenericTree {

    protected final Random random;
    protected int height;
    protected final BlockStateDelegate delegate;
    protected Collection<Material> overridables;
    private final int logType;
    private final int leavesType;
    private final boolean cocoaTree;

    public GenericTree(Random random, BlockStateDelegate delegate) {
        this(random, 4, 0, 0, delegate);
    }

    public GenericTree(Random random, int minHeight, int logType, int leavesType, BlockStateDelegate delegate) {
        this(random, minHeight, logType, leavesType, false, delegate);
    }

    public GenericTree(Random random, int minHeight, int logType, int leavesType, boolean cocoaTree, BlockStateDelegate delegate) {
        this.random = random;
        this.logType = logType;
        this.leavesType = leavesType;
        this.cocoaTree = cocoaTree;
        this.delegate = delegate;
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
        setHeight(random.nextInt(3) + minHeight);
    }

    protected final void setOverridables(Material... overridables) {
        this.overridables = Arrays.asList(overridables);
    }

    protected final void setHeight(int height) {
        this.height = height;
    }

    public boolean canHeightFitAt(int sourceY) {
        return sourceY >= 1 && sourceY + height + 1 <= 255;
    }

    public boolean canPlaceOn(World world, int x, int y, int z) {
        final BlockState state = delegate.getBlockState(world, x, y, z);
        return state.getType() == Material.GRASS ||
                state.getType() == Material.DIRT ||
                state.getType() == Material.SOIL;
    }

    public boolean canPlaceAt(World world, int sourceX, int sourceY, int sourceZ) {
        for (int y = sourceY; y <= sourceY + 1 + height; y++) {
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == sourceY) {
                radius = 0; // radius at source block y is 0 (only trunk)
            } else if (y >= sourceY + 1 + height - 2) {
                radius = 2; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        final Material type = delegate.getBlockState(world, x, y, z).getType();
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

    public boolean generate(World world, int sourceX, int sourceY, int sourceZ) {
        // check height range
        if (!canHeightFitAt(sourceY)) {
            return false;
        }

        // check below block
        if (!canPlaceOn(world, sourceX, sourceY - 1, sourceZ)) {
            return false;
        }

        // check for sufficient space around
        if (!canPlaceAt(world, sourceX, sourceY, sourceZ)) {
            return false;
        }

        // generate the leaves
        for (int y = sourceY + height - 3; y <= sourceY + height; y++) {
            int n = y - (sourceY + height);
            int radius = 1 - n / 2;
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if (Math.abs(x - sourceX) != radius || Math.abs(z - sourceZ) != radius
                            || (random.nextBoolean() && n != 0)) {
                        final Material material = delegate.getBlockState(world, x, y, z).getType();
                        if (material == Material.AIR || material == Material.LEAVES) {
                            delegate.setTypeAndRawData(world, x, y, z, Material.LEAVES, leavesType);
                        }
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            final Material material = delegate.getBlockState(world, sourceX, sourceY + y, sourceZ).getType();
            if (material == Material.AIR || material == Material.LEAVES) {
                delegate.setTypeAndRawData(world, sourceX, sourceY + y, sourceZ, Material.LOG, logType);
            }
        }

        if (cocoaTree) {
            // places some vines on the trunk
            addVinesOnTrunk(world, sourceX, sourceY, sourceZ);
            // search for air around leaves to grow hanging vines
            addVinesOnLeaves(world, sourceX, sourceY, sourceZ);
            // and maybe place some cocoa
            addCocoa(world, sourceX, sourceY, sourceZ);
        }

        // block below trunk is always dirt
        delegate.setTypeAndRawData(world, sourceX, sourceY - 1, sourceZ, Material.DIRT, 0);

        return true;
    }

    private void addVinesOnTrunk(World world, int sourceX, int sourceY, int sourceZ) {
        for (int y = 1; y < height; y++) {
            if (random.nextInt(3) != 0
                    && delegate.getBlockState(world, sourceX - 1, sourceY + y, sourceZ).getType() == Material.AIR) {
                delegate.setTypeAndData(world, sourceX - 1, sourceY + y, sourceZ, Material.VINE, new Vine(BlockFace.EAST));
            }
            if (random.nextInt(3) != 0
                    && delegate.getBlockState(world, sourceX + 1, sourceY + y, sourceZ).getType() == Material.AIR) {
                delegate.setTypeAndData(world, sourceX + 1, sourceY + y, sourceZ, Material.VINE, new Vine(BlockFace.WEST));
            }
            if (random.nextInt(3) != 0
                    && delegate.getBlockState(world, sourceX, sourceY + y, sourceZ - 1).getType() == Material.AIR) {
                delegate.setTypeAndData(world, sourceX, sourceY + y, sourceZ - 1, Material.VINE, new Vine(BlockFace.SOUTH));
            }
            if (random.nextInt(3) != 0
                    && delegate.getBlockState(world, sourceX, sourceY + y, sourceZ + 1).getType() == Material.AIR) {
                delegate.setTypeAndData(world, sourceX, sourceY + y, sourceZ + 1, Material.VINE, new Vine(BlockFace.NORTH));
            }
        }
    }

    private void addVinesOnLeaves(World world, int sourceX, int sourceY, int sourceZ) {
        for (int y = sourceY - 3 + height; y <= sourceY + height; y++) {
            int nY = y - (sourceY + height);
            int radius = 2 - nY / 2;
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if (delegate.getBlockState(world, x, y, z).getType() == Material.LEAVES) {
                        if (random.nextInt(4) == 0
                                && delegate.getBlockState(world, x - 1, y, z).getType() == Material.AIR) {
                            addHangingVine(world, x - 1, y, z, BlockFace.EAST);
                        }
                        if (random.nextInt(4) == 0
                                && delegate.getBlockState(world, x + 1, y, z).getType() == Material.AIR) {
                            addHangingVine(world, x + 1, y, z, BlockFace.WEST);
                        }
                        if (random.nextInt(4) == 0
                                && delegate.getBlockState(world, x, y, z - 1).getType() == Material.AIR) {
                            addHangingVine(world, x, y, z - 1, BlockFace.SOUTH);
                        }
                        if (random.nextInt(4) == 0
                                && delegate.getBlockState(world, x, y, z + 1).getType() == Material.AIR) {
                            addHangingVine(world, x, y, z + 1, BlockFace.NORTH);
                        }
                    }
                }
            }
        }
    }

    private void addCocoa(World world, int sourceX, int sourceY, int sourceZ) {
        if (height > 5 && random.nextInt(5) == 0) {
            for (int y = 0; y < 2; y++) {
                for (int i = 0; i < 4; i++) {         // rotate the 4 trunk faces
                    if (random.nextInt(4 - y) == 0) { // higher it is, more chances there is
                        final BlockFace face = getCocoaFace(i);
                        final CocoaPlantSize size = getCocoaSize(random.nextInt(3));
                        final Block block = delegate.getBlockState(world, sourceX, sourceY + height - 5 + y, sourceZ)
                                .getBlock().getRelative(face);
                        delegate.setTypeAndData(world, block.getX(), block.getY(), block.getZ(),
                                Material.COCOA, new CocoaPlant(size, face.getOppositeFace()));
                    }
                }
            }
        }
    }

    private void addHangingVine(World world, int x, int y, int z, BlockFace face) {
        for (int i = 0; i < 5; i++) {
            if (delegate.getBlockState(world, x, y - i, z).getType() != Material.AIR) {
                break;
            }
            delegate.setTypeAndData(world, x, y - i, z, Material.VINE, new Vine(face));
        }
    }

    private BlockFace getCocoaFace(int n) {
        switch (n) {
            case 0:
                return BlockFace.NORTH;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.SOUTH;
            case 3:
                return BlockFace.WEST;
            default:
                return BlockFace.NORTH;
        }
    }

    private CocoaPlantSize getCocoaSize(int n) {
        switch (n) {
            case 0:
                return CocoaPlantSize.SMALL;
            case 1:
                return CocoaPlantSize.MEDIUM;
            case 2:
                return CocoaPlantSize.LARGE;
            default:
                return CocoaPlantSize.SMALL;
        }
    }
}
