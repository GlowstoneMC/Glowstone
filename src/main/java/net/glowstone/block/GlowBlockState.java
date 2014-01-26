package net.glowstone.block;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Represents a state a block could be in as well as any tile entities.
 */
public class GlowBlockState implements BlockState {

    private final GlowWorld world;
    private final GlowChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    private final byte light;
    protected int type;
    protected MaterialData data;

    public GlowBlockState(GlowBlock block) {
        world = block.getWorld();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        type = block.getTypeId();
        light = block.getLightLevel();
        chunk = block.getChunk();
        makeData(block.getData());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basics

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

    public Location getLocation() {
        return getBlock().getLocation();
    }

    public Location getLocation(Location loc) {
        return getBlock().getLocation(loc);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type and data

    final public Material getType() {
        return Material.getMaterial(type);
    }

    final public void setType(Material type) {
        setTypeId(type.getId());
    }

    final public int getTypeId() {
        return type;
    }

    final public boolean setTypeId(int type) {
        this.type = type;
        makeData((byte) 0);
        return true;
    }

    final public MaterialData getData() {
        return data;
    }

    final public void setData(MaterialData data) {
        this.data = data;
    }

    final public byte getRawData() {
        return getData().getData();
    }

    final public void setRawData(byte data) {
        getData().setData(data);
    }

    final public byte getLightLevel() {
        return light;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Update

    public boolean update() {
        return update(false, true);
    }

    public boolean update(boolean force) {
        return update(force, true);
    }

    public boolean update(boolean force, boolean applyPhysics) {
        Block block = getBlock();

        return (block.getTypeId() == type || force) && block.setTypeIdAndData(type, getRawData(), applyPhysics);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private void makeData(byte data) {
        Material mat = Material.getMaterial(type);
        if (mat == null) {
            this.data = new MaterialData(type, data);
        } else {
            this.data = mat.getNewData(data);
        }
    }

    /**
     * Create a shallow clone which refers to the same underlying tile entity but keeps
     * a different MaterialData instance.
     */
    public GlowBlockState shallowClone() {
        return new GlowBlockState(getBlock());
    }

    public void destroy() {
        throw new IllegalStateException("Cannot destroy a generic BlockState");
    }

    public void update(GlowPlayer player) {
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        getBlock().setMetadata(metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return getBlock().getMetadata(metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return getBlock().hasMetadata(metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        getBlock().removeMetadata(metadataKey, owningPlugin);
    }
}
