package net.glowstone.block;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
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

    @Override
    public GlowWorld getWorld() {
        return world;
    }

    @Override
    public GlowChunk getChunk() {
        return chunk;
    }

    @Override
    public GlowBlock getBlock() {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public Location getLocation() {
        return getBlock().getLocation();
    }

    @Override
    public Location getLocation(Location loc) {
        return getBlock().getLocation(loc);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type and data

    @Override
    final public Material getType() {
        return Material.getMaterial(type);
    }

    @Override
    final public void setType(Material type) {
        setTypeId(type.getId());
    }

    @Override
    final public int getTypeId() {
        return type;
    }

    @Override
    final public boolean setTypeId(int type) {
        this.type = type;
        makeData((byte) 0);
        return true;
    }

    @Override
    final public MaterialData getData() {
        return data;
    }

    @Override
    final public void setData(MaterialData data) {
        this.data = data;
    }

    @Override
    final public byte getRawData() {
        return getData().getData();
    }

    @Override
    final public void setRawData(byte data) {
        getData().setData(data);
    }

    @Override
    final public byte getLightLevel() {
        return light;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Update

    @Override
    public final boolean update() {
        return update(false, true);
    }

    @Override
    public final boolean update(boolean force) {
        return update(force, true);
    }

    @Override
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

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        getBlock().setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return getBlock().getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return getBlock().hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        getBlock().removeMetadata(metadataKey, owningPlugin);
    }
}
