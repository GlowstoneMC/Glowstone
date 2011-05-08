package net.glowstone.block;

import net.glowstone.GlowChunk;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Tad
 */
public class GlowBlockState implements BlockState {

    private final World world;
    private final GlowChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    protected int type;
    protected MaterialData data;
    protected byte light;

    public GlowBlockState(final Block block) {
        world = block.getWorld();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        type = block.getTypeId();
        light = block.getLightLevel();
        chunk = (GlowChunk) block.getChunk();
    }

    // Basic getters

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Block getBlock() {
        return world.getBlockAt(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    // Type and data

    public MaterialData getData() {
        return data;
    }

    public void setData(MaterialData data) {
        this.data = data;
    }

    public Material getType() {
        return Material.getMaterial(type);
    }

    public int getTypeId() {
        return type;
    }

    public void setType(Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(int type) {
        this.type = type;
        return true;
    }

    public byte getLightLevel() {
        return light;
    }

    public byte getRawData() {
        return data.getData();
    }

    // Update

    public boolean update() {
        return update(false);
    }

    public boolean update(boolean force) {
        Block block = getBlock();

        if (block.getTypeId() != type) {
            if (force) {
                block.setTypeId(type);
            } else {
                return false;
            }
        }

        block.setData(data.getData());
        return true;
    }

}
