package net.glowstone.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.glowstone.GlowWorld;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a state a block could be in as well as any block entities.
 */
@Data
public class GlowBlockState implements BlockState {

    private final GlowWorld world;
    private final int x;
    private final int y;
    private final int z;
    @EqualsAndHashCode.Exclude
    private final byte lightLevel;

    @EqualsAndHashCode.Include
    protected Material type;
    @EqualsAndHashCode.Include
    protected MaterialData data;
    private boolean flowed;

    ////////////////////////////////////////////////////////////////////////////
    // Basics

    /**
     * Creates a BlockState object to track the given block's state.
     *
     * @param block the block
     */
    public GlowBlockState(GlowBlock block) {
        world = block.getWorld();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        type = block.getType();
        lightLevel = block.getLightLevel();
        makeData(block.getData());
    }

    @Override
    public GlowChunk getChunk() {
        return getBlock().getChunk();
    }

    @Override
    public GlowBlock getBlock() {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return getBlock().getBlockData();
    }

    @Override
    public void setBlockData(@NotNull BlockData data) {
        getBlock().setBlockData(data);
    }

    @Override
    public Location getLocation() {
        return getBlock().getLocation();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type and data

    @Override
    public Location getLocation(Location loc) {
        return getBlock().getLocation(loc);
    }

    @Override
    public final void setType(@NotNull Material type) {
        if (this.type == type) {
            return;
        }
        Material old = this.type;
        this.type = type;
        if (old.getData().equals(type.getData())) {
            setRawData((byte) 0);
        } else {
            makeData((byte) 0);
        }
    }

    @Override
    public final byte getRawData() {
        return getData().getData();
    }

    @Override
    public final void setRawData(byte data) {
        getData().setData(data);
    }

    @Override
    public boolean isPlaced() {
        return world != null; // TODO: is this sufficient?
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
        if (block.getType() == getType() || force) {
            block.setBlockData(block.getBlockData(), applyPhysics);
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private void makeData(byte data) {
        if (type == null) {
            this.data = new MaterialData(type, data);
        } else {
            this.data = type.getNewData(data);
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
