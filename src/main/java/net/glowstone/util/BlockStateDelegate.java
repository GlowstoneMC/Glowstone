package net.glowstone.util;

import java.util.Collection;
import java.util.HashMap;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

/**
 * A small utility class that allow to maintain a {@link BlockState}'s list in order to capture a
 * chain of modified blocks and update all the states in once, or never (ie: event cancelled).
 */
public class BlockStateDelegate {

    private final HashMap<Location, BlockState> blockStateMap = new HashMap<>();
    private final HashMap<Location, BlockState> blockStateBackupMap = new HashMap<>();

    /**
     * Sets a block type and add it to the BlockState list.
     *
     * @param world the world which contains the block
     * @param x     the x-coordinate of this block
     * @param y     the y-coordinate of this block
     * @param z     the z-coordinate of this block
     * @param type  the new type of this block
     */
    public void setType(World world, int x, int y, int z, Material type) {
        GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
        state.setType(type);
        blockStateMap.put(world.getBlockAt(x, y, z).getLocation(), state);
    }

    /**
     * Sets a block type and MaterialData, and add it to the BlockState list.
     *
     * @param world the world which contains the block
     * @param x     the x-coordinate of this block
     * @param y     the y-coordinate of this block
     * @param z     the z-coordinate of this block
     * @param type  the new type of this block
     * @param data  the new MaterialData of this block
     */
    public void setTypeAndData(World world, int x, int y, int z, Material type, BlockData data) {
        // TODO: we probably don't need the type param, just use block data
        GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
        state.setType(type);
        state.setBlockData(data);
        blockStateMap.put(world.getBlockAt(x, y, z).getLocation(), state);
    }

    /**
     * Backups a block state.
     *
     * @param block the block which state should be backup
     */
    public void backupBlockState(Block block) {
        blockStateMap.remove(block.getLocation());
        blockStateBackupMap.put(block.getLocation(), new GlowBlockState((GlowBlock) block));
    }

    /**
     * Roll-back previously backed-up block states.
     */
    public void rollbackBlockStates() {
        for (BlockState state : blockStateBackupMap.values()) {
            state.update(true);
        }
        blockStateBackupMap.clear();
    }

    /**
     * Returns the BlockState list.
     *
     * @return A list with all {@link BlockState}.
     */
    public Collection<BlockState> getBlockStates() {
        return blockStateMap.values();
    }

    /**
     * Updates all block states contained in the BlockState list.
     */
    public void updateBlockStates() {
        for (BlockState state : blockStateMap.values()) {
            state.update(true);
        }
        blockStateMap.clear();
    }

    /**
     * Returns the {@link BlockState} of a block at the given coordinates.
     *
     * @param world the world which contains the block
     * @param x     the x-coordinate
     * @param y     the y-coordinate
     * @param z     the z-coordinate
     * @return The {@link BlockState} state.
     */
    public BlockState getBlockState(World world, int x, int y, int z) {
        Location loc = world.getBlockAt(x, y, z).getLocation();
        if (blockStateMap.containsKey(loc)) {
            return blockStateMap.get(loc);
        } else {
            return loc.getBlock().getState();
        }
    }

    /**
     * Returns the {@link BlockState} of a block at the given location.
     *
     * @param loc the location which contains the block
     * @return The {@link BlockState} state.
     */
    public BlockState getBlockState(Location loc) {
        if (blockStateMap.containsKey(loc)) {
            return blockStateMap.get(loc);
        } else {
            return loc.getBlock().getState();
        }
    }
}
