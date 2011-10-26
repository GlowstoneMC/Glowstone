package net.glowstone.block;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

/**
 * Represents a state a block could be in as well as any tile entities.
 */
public class GlowBlockState implements BlockState {

    private final GlowWorld world;
    private final GlowChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    protected int type;
    protected MaterialData data;
    protected byte light;

    public GlowBlockState(GlowBlock block) {
        world = block.getWorld();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        type = block.getTypeId();
        light = block.getLightLevel();
        chunk = (GlowChunk) block.getChunk();
        makeData(block.getData());
    }

    // Basic getters

    public GlowWorld getWorld() {
        return world;
    }

    public GlowChunk getChunk() {
        return chunk;
    }

    public GlowBlock getBlock() {
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

    final public Material getType() {
        return Material.getMaterial(type);
    }

    final public int getTypeId() {
        return type;
    }

    final public void setType(Material type) {
        setTypeId(type.getId());
    }

    final public boolean setTypeId(int type) {
        this.type = type;
        makeData((byte) 0);
        return true;
    }

    final public byte getLightLevel() {
        return light;
    }

    final public byte getRawData() {
        return getData().getData();
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

        block.setData(getRawData());
        return true;
    }

    public void update(GlowPlayer player) {}
    
    // Internal mechanisms

    private void makeData(byte data) {
        Material mat = Material.getMaterial(type);
        if (mat == null || mat.getData() == null) {
            this.data = new MaterialData(type, data);
        } else {
            this.data = mat.getNewData(data);
        }
    }
    
    public GlowBlockState shallowClone() {
        return getBlock().getState();
    }
    
    public void destroy() {
        throw new IllegalStateException("Cannot destroy a generic BlockState");
    }

}
