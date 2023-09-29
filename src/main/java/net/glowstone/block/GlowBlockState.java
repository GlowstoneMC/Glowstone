package net.glowstone.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.GlowWorld;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

/**
 * Represents a state a block could be in as well as any block entities.
 */
@Data
public class GlowBlockState implements BlockState, TileState {

    private final GlowWorld world;
    private final int x;
    private final int y;
    private final int z;
    @EqualsAndHashCode.Exclude
    private final byte lightLevel;

    @EqualsAndHashCode.Include
    protected Material type;
    @Getter
    @Setter
    protected MaterialData data;
    @EqualsAndHashCode.Include
    protected BlockData blockData;
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
        blockData = block.getBlockData();
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
        this.type = type;
        setBlockData(type.createBlockData());
    }

    @Override
    public final byte getRawData() {
        return (byte) 0;
    }

    @Override
    public final void setRawData(byte data) {
    }

    @Override
    public boolean isPlaced() {
        return world != null; // TODO: is this sufficient?
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public @Unmodifiable @NotNull Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public @Unmodifiable @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack tool) {
        return null;
    }

    @Override
    public @Unmodifiable @NotNull Collection<ItemStack> getDrops(@NotNull ItemStack tool, @Nullable Entity entity) {
        return null;
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
            block.setBlockData(blockData, applyPhysics);
            return true;
        }
        return false;
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

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return null;
    }

    @Override
    public boolean isSnapshot() {
        return false;
    }
}
