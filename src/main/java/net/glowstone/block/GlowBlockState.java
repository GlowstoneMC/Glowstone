package net.glowstone.block;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.GlowWorld;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Represents a state a block could be in as well as any block entities.
 */
public class GlowBlockState implements BlockState {

    @Getter
    private final GlowWorld world;
    @Getter
    private final int x;
    @Getter
    private final int y;
    @Getter
    private final int z;
    @Getter
    private final byte lightLevel;
    @Getter
    protected int typeId;
    @Getter
    @Setter
    protected MaterialData data;
    @Getter
    @Setter
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
        typeId = block.getTypeId();
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
    public final Material getType() {
        return Material.getMaterial(typeId);
    }

    @Override
    public final void setType(Material type) {
        setTypeId(type.getId());
    }

    @Override
    public final boolean setTypeId(int type) {
        if (this.typeId == type) {
            return false;
        }
        Material oldType = Material.getMaterial(typeId);
        Material newType = Material.getMaterial(type);
        this.typeId = type;
        if (oldType.getData().equals(newType.getData())) {
            setRawData((byte) 0);
        } else {
            makeData((byte) 0);
        }
        return true;
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

        return (block.getTypeId() == typeId || force)
                && block.setTypeIdAndData(typeId, getRawData(), applyPhysics);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private void makeData(byte data) {
        Material mat = Material.getMaterial(typeId);
        if (mat == null) {
            this.data = new MaterialData(typeId, data);
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

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (world != null ? world.hashCode() : 0);
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        result = prime * result + typeId;
        result = prime * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GlowBlockState other = (GlowBlockState) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (typeId != other.typeId) {
            return false;
        }
        if (world == null) {
            if (other.world != null) {
                return false;
            }
        } else if (!world.equals(other.world)) {
            return false;
        }
        return x == other.x && y == other.y && z == other.z;
    }
}
