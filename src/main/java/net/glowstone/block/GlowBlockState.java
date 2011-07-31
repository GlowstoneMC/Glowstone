package net.glowstone.block;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;

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

        block.setData(data.getData());
        return true;
    }
    
    // Internal mechanisms
    
    public void destroy() {
        throw new UnsupportedOperationException("Cannot destroy a generic BlockState");
    }
    
    public void load(CompoundTag compound) {
        throw new UnsupportedOperationException("Cannot load to a generic BlockState");
    }
    
    public CompoundTag save() {
        throw new UnsupportedOperationException("Cannot save from a generic BlockState");
    }
    
    protected void load(CompoundTag compound, String id) {
        String checkId = ((StringTag) compound.getValue().get("id")).getValue();
        if (!id.equalsIgnoreCase(checkId)) {
            throw new IllegalArgumentException("Invalid ID loading tile entity, expected " + id + " got " + checkId);
        }
        int checkX = ((IntTag) compound.getValue().get("x")).getValue();
        int checkY = ((IntTag) compound.getValue().get("y")).getValue();
        int checkZ = ((IntTag) compound.getValue().get("z")).getValue();
        if (x != checkX || y != checkY || z != checkZ) {
            throw new IllegalArgumentException("Invalid coords loading tile entity, expected (" + x + "," + y + "," + z + ") got (" + checkX + "," + checkY + "," + checkZ + ")");
        }
    }
    
    protected Map<String, Tag> save(String id) {
        Map<String, Tag> result = new HashMap<String, Tag>();
        result.put("id", new StringTag("id", id));
        result.put("x", new IntTag("x", x));
        result.put("y", new IntTag("y", y));
        result.put("z", new IntTag("z", z));
        return result;
    }

}
