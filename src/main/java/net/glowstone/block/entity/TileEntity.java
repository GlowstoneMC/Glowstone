package net.glowstone.block.entity;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.block.Block;

/**
 * Base class for tile entities (blocks with NBT data) in the world.
 * Most access to tile entities should occur through the Bukkit BlockState API.
 */
public abstract class TileEntity {

    protected final GlowBlock block;
    private String saveId;

    /**
     * Create a new TileEntity at the given location.
     * @param block The block the TileEntity is attached to.
     */
    public TileEntity(GlowBlock block) {
        this.block = block;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility stuff

    /**
     * Get the block this TileEntity is associated with.
     * @return The entity's block.
     */
    public final Block getBlock() {
        return block;
    }

    /**
     * Update this TileEntity's visible state to all players in range.
     */
    public final void updateInRange() {
        GlowChunk.Key key = new GlowChunk.Key(block.getChunk().getX(), block.getChunk().getZ());
        for (GlowPlayer player : block.getWorld().getRawPlayers()) {
            if (player.canSeeChunk(key)) {
                update(player);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // World I/O

    /**
     * Set the text ID this tile entity is saved to disk with. If this is not
     * set, then load and save of the "id" tag must be performed manually.
     * @param saveId The ID.
     */
    protected final void setSaveId(String saveId) {
        if (this.saveId != null) {
            throw new IllegalStateException("Can only set saveId once");
        }
        this.saveId = saveId;
    }

    /**
     * Read this TileEntity's data from the saved tag.
     * @param tag The tag to load from.
     */
    public void loadNbt(CompoundTag tag) {
        // verify id and coordinates
        if (saveId != null) {
            if (!tag.isString("id") || !tag.getString("id").equals(saveId)) {
                throw new IllegalArgumentException("Expected tile entity id of " + saveId + ", got " + tag.getString("id"));
            }
        }

        // verify coordinates if provided
        if (tag.isInt("x")) {
            int x = tag.getInt("x");
            int y = tag.getInt("y");
            int z = tag.getInt("z");
            int rx = block.getX(), ry = block.getY(), rz = block.getZ();
            if (x != rx || y != ry || z != rz) {
                throw new IllegalArgumentException("Tried to load tile entity with coords (" + x + "," + y + "," + z + ") into (" + rx + "," + ry + "," + rz + ")");
            }
        }
    }

    /**
     * Save this TileEntity's data to NBT.
     * @param tag The tag to save to.
     */
    public void saveNbt(CompoundTag tag) {
        if (saveId != null) {
            tag.putString("id", saveId);
        }
        tag.putInt("x", block.getX());
        tag.putInt("y", block.getY());
        tag.putInt("z", block.getZ());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overridable stuff

    /**
     * Create a new BlockState which will correspond to this tile entity.
     * @return A GlowBlockState, or null to use a standard BlockState.
     */
    public GlowBlockState getState() {
        return null;
    }

    /**
     * Destroy this TileEntity.
     */
    public void destroy() {
        // nothing by default
    }

    /**
     * Update this TileEntity's visible state to the given player.
     * @param player The player to update.
     */
    public void update(GlowPlayer player) {
        // nothing by default
    }
}
