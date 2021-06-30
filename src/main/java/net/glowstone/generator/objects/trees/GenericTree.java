package net.glowstone.generator.objects.trees;

import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import net.glowstone.generator.objects.TerrainObject;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

/**
 * Oak tree, and superclass for other types.
 */
public class GenericTree implements TerrainObject {

    protected static final Set<Material> LEAF_TYPES = Sets.immutableEnumSet(
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.ACACIA_LEAVES,
            Material.DARK_OAK_LEAVES
    );

    protected final BlockStateDelegate delegate;
    protected int height;
    protected Material logType;
    protected Material leavesType;
    protected Collection<Material> overridables;

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill in wood and leaves
     */
    public GenericTree(Random random, BlockStateDelegate delegate) {
        this.delegate = delegate;
        setOverridables(
                Material.AIR,
                Material.DIRT,
                Material.GRASS_BLOCK,
                Material.VINE,
                // Leaves
                Material.OAK_LEAVES,
                Material.SPRUCE_LEAVES,
                Material.BIRCH_LEAVES,
                Material.JUNGLE_LEAVES,
                Material.ACACIA_LEAVES,
                Material.DARK_OAK_LEAVES,
                // Logs
                Material.OAK_LOG,
                Material.SPRUCE_LOG,
                Material.BIRCH_LOG,
                Material.JUNGLE_LOG,
                Material.ACACIA_LOG,
                Material.DARK_OAK_LOG,
                // Saplings
                Material.OAK_SAPLING,
                Material.SPRUCE_SAPLING,
                Material.BIRCH_SAPLING,
                Material.JUNGLE_SAPLING,
                Material.ACACIA_SAPLING,
                Material.DARK_OAK_SAPLING
        );
        setHeight(random.nextInt(3) + 4);
        setTypes(Material.OAK_LOG, Material.OAK_LEAVES);
    }

    protected final void setOverridables(Material... overridables) {
        this.overridables = Arrays.asList(overridables);
    }

    protected final void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the block data values for this tree's blocks.
     *
     * @param logType    the species portion of the data value for wood blocks.
     * @param leavesType the species portion of the data value for leaf blocks.
     */
    protected final void setTypes(Material logType, Material leavesType) {
        this.logType = logType;
        this.leavesType = leavesType;
    }

    /**
     * Checks whether this tree fits under the upper world limit.
     *
     * @param baseHeight the height of the base of the trunk
     * @return true if this tree can grow without exceeding block height 255; false otherwise.
     */
    public boolean canHeightFit(int baseHeight) {
        return baseHeight >= 1 && baseHeight + height + 1 <= 255;
    }

    /**
     * Checks whether this tree can grow on top of the given block.
     *
     * @param soil the block we're growing on
     * @return true if this tree can grow on the type of block below it; false otherwise
     */
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS_BLOCK
                || soil.getType() == Material.DIRT
                || soil.getType() == Material.COARSE_DIRT
                || soil.getType() == Material.FARMLAND;
    }

    /**
     * Checks whether this tree has enough space to grow.
     *
     * @param baseX the X coordinate of the base of the trunk
     * @param baseY the Y coordinate of the base of the trunk
     * @param baseZ the Z coordinate of the base of the trunk
     * @param world the world to grow in
     * @return true if this tree has space to grow; false otherwise
     */
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == baseY) {
                radius = 0; // radius at source block y is 0 (only trunk)
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

    /**
     * Attempts to grow this tree at its current location. If successful, the associated {@link
     * BlockStateDelegate} is instructed to set blocks to wood and leaves.
     *
     * @param loc the base of the trunk
     * @return true if successfully grown; false otherwise
     * @deprecated use {@link #generate(World, Random, int, int, int)}
     */
    @Deprecated
    public boolean generate(Location loc) {
        return generate(loc.getWorld(), ThreadLocalRandom.current(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generate the leaves
        for (int y = blockY + height - 3; y <= blockY + height; y++) {
            int n = y - (blockY + height);
            int radius = 1 - n / 2;
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    if (Math.abs(x - blockX) != radius
                            || Math.abs(z - blockZ) != radius
                            || random.nextBoolean() && n != 0) {
                        replaceIfAirOrLeaves(x, y, z, leavesType, world);
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            replaceIfAirOrLeaves(blockX, blockY + y, blockZ, logType, world);
        }

        // block below trunk is always dirt
        delegate.setType(world, blockX, blockY - 1, blockZ, Material.DIRT);
        return true;
    }

    /**
     * Returns whether any of {@link #canHeightFit(int)}, {@link #canPlace(int, int, int, World)} or
     * {@link #canPlaceOn(BlockState)} prevent this tree from generating.
     *
     * @param baseX the X coordinate of the base of the trunk
     * @param baseY the Y coordinate of the base of the trunk
     * @param baseZ the Z coordinate of the base of the trunk
     * @param world the world to grow in
     * @return true if any of the checks prevent us from generating, false otherwise
     */
    protected boolean cannotGenerateAt(int baseX, int baseY, int baseZ,
                                       World world) {
        return !canHeightFit(baseY)
                || !canPlaceOn(world.getBlockAt(baseX, baseY - 1, baseZ).getState())
                || !canPlace(baseX, baseY, baseZ, world);
    }

    /**
     * Replaces the block at a location with the given new one, if it is air or leaves.
     *
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param z           the z coordinate
     * @param newMaterial the new block type
     * @param world       the world we are generating in
     */
    protected void replaceIfAirOrLeaves(int x, int y, int z, Material newMaterial,
                                        World world) {
        Material oldMaterial = blockTypeAt(x, y, z, world);
        if (oldMaterial == Material.AIR || LEAF_TYPES.contains(oldMaterial)) {
            delegate.setType(world, x, y, z, newMaterial);
        }
    }

    /**
     * Returns the block type at the given coordinates.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @param world the world we are generating in
     * @return the block type
     */
    protected Material blockTypeAt(int x, int y, int z, World world) {
        return delegate.getBlockState(world, x, y, z).getType();
    }
}
