package net.glowstone.generator.objects.trees;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import net.glowstone.generator.objects.TerrainObject;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

/** Oak tree, and superclass for other types. */
public class GenericTree implements TerrainObject {

    protected final Random random;
    protected final BlockStateDelegate delegate;
    protected int height;
    protected int logType;
    protected int leavesType;
    protected Collection<Material> overridables;

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *  @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public GenericTree(Random random, BlockStateDelegate delegate) {
        this.random = random;
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
        setHeight(random.nextInt(3) + 4);
        setTypes(0, 0);
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
     * @param logType the species portion of the data value for wood blocks.
     * @param leavesType the species portion of the data value for leaf blocks.
     */
    protected final void setTypes(int logType, int leavesType) {
        this.logType = logType;
        this.leavesType = leavesType;
    }

    /**
     * Checks whether this tree fits under the upper world limit.
     * @param baseHeight the height of the base of the trunk
     *
     * @return true if this tree can grow without exceeding block height 255; false otherwise.
     */
    public boolean canHeightFit(int baseHeight) {
        return baseHeight >= 1 && baseHeight + height + 1 <= 255;
    }

    /**
     * Checks whether this tree is on fertile ground.
     * @param loc the base of the trunk
     *
     * @return true if this tree can grow on the type of block below it; false otherwise
     */
    public boolean canPlaceOn(Location loc) {
        BlockState state = delegate
            .getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT
            || state.getType() == Material.SOIL;
    }

    /**
     * Checks whether this tree has enough space to grow.
     *
     * @param loc the location to grow at
     * @return true if this tree has space to grow; false otherwise
     */
    public boolean canPlace(Location loc) {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement
            int radius = 1; // default radius if above first block
            if (y == loc.getBlockY()) {
                radius = 0; // radius at source block y is 0 (only trunk)
            } else if (y >= loc.getBlockY() + 1 + height - 2) {
                radius = 2; // max radius starting at leaves bottom
            }
            // check for block collision on horizontal slices
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // we can overlap some blocks around
                        Material type = blockTypeAt(x, y, z, loc.getWorld());
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
     */
    public boolean generate(Location loc) {
        if (cannotGenerateAt(loc)) {
            return false;
        }

        // generate the leaves
        for (int y = loc.getBlockY() + height - 3; y <= loc.getBlockY() + height; y++) {
            int n = y - (loc.getBlockY() + height);
            int radius = 1 - n / 2;
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (Math.abs(x - loc.getBlockX()) != radius
                        || Math.abs(z - loc.getBlockZ()) != radius
                        || random.nextBoolean() && n != 0) {
                        replaceIfAirOrLeaves(x, y, z, Material.LEAVES, leavesType, loc.getWorld());
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < height; y++) {
            replaceIfAirOrLeaves(loc.getBlockX(),
                    loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType, loc.getWorld());
        }

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(),
                Material.DIRT, dirt);

        return true;
    }

    /**
     * Returns whether any of {@link #canHeightFit(int)}, {@link #canPlace(Location)} or {@link
     * #canPlaceOn(Location)} prevent this tree from generating.
     *
     * @param loc the location where we are to generate
     * @return true if any of the checks prevent us from generating, false otherwise
     */
    protected boolean cannotGenerateAt(Location loc) {
        return !canHeightFit(loc.getBlockY()) || !canPlaceOn(loc) || !canPlace(loc);
    }

    /**
     * Replaces the block at a location with the given new one, if it is air or leaves.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param newMaterial the new block type
     * @param data the new block data
     * @param world the world we are generating in
     */
    protected void replaceIfAirOrLeaves(int x, int y, int z, Material newMaterial, int data,
            World world) {
        Material oldMaterial = blockTypeAt(x, y, z, world);
        if (oldMaterial == Material.AIR || oldMaterial == Material.LEAVES) {
            delegate.setTypeAndRawData(world, x, y, z, newMaterial, data);
        }
    }

    /**
     * Returns the block type at the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param world the world we are generating in
     * @return the block type
     */
    protected Material blockTypeAt(int x, int y, int z, World world) {
        return delegate.getBlockState(
                world, x, y,
                z).getType();
    }

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        return generate(new Location(world, sourceX, sourceY, sourceZ));
    }
}
