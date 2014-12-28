package net.glowstone.util;

import net.glowstone.block.GlowBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import java.util.Collection;
import java.util.HashMap;

/**
 * A small utility class that allow to maintain a {@link BlockState}'s
 * list in order to capture a chain of modified blocks and update all
 * the states in once, or never (ie: event cancelled).
 */
public class BlockStateDelegate {

    private final HashMap<Location, BlockState> blockStateMap = new HashMap<Location, BlockState>();

    /**
     * Sets a block type and add it to the BlockState list.
     * @param world the world which contains the block
     * @param x the x-coordinate of this block
     * @param y the y-coordinate of this block
     * @param z the z-coordinate of this block
     * @param type the new type of this block
     */
    public void setType(World world, int x, int y, int z, Material type) {
        final GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
        state.setType(type);
        blockStateMap.put(world.getBlockAt(x, y, z).getLocation(), state);
    }

    /**
     * Sets a block type and MaterialData, and add it to the BlockState list.
     * @param world the world which contains the block
     * @param x the x-coordinate of this block
     * @param y the y-coordinate of this block
     * @param z the z-coordinate of this block
     * @param type the new type of this block
     * @param data the new MaterialData of this block
     */
    public void setTypeAndData(World world, int x, int y, int z, Material type, MaterialData data) {
        final GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
        state.setType(type);
        state.setData(data);
        blockStateMap.put(world.getBlockAt(x, y, z).getLocation(), state);
    }

    /**
     * Sets a block type, data and add it to the BlockState list.
     * @param world the world which contains the block
     * @param x the x-coordinate of this block
     * @param y the y-coordinate of this block
     * @param z the z-coordinate of this block
     * @param type the new type of this block
     * @param data the new data value of this block
     */
    public void setTypeAndRawData(World world, int x, int y, int z, Material type, int data) {
        final GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
        state.setType(type);
        state.setRawData((byte) data);
        blockStateMap.put(world.getBlockAt(x, y, z).getLocation(), state);
    }

    /**
     * Returns the BlockState list
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
    }

    /**
     * Returns the {@link BlockState} of a block at the given coordinates
     * @param world the world which contains the block
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return The {@link BlockState} state.
     */
    public BlockState getBlockState(World world, int x, int y, int z) {
        final Location loc = world.getBlockAt(x, y, z).getLocation();
        if (blockStateMap.containsKey(loc)) {
            return blockStateMap.get(loc);
        } else {
            return loc.getBlock().getState();
        }
    }

    /**
     * Returns the {@link BlockState} of a block at the given location
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
