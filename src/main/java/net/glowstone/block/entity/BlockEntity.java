package net.glowstone.block.entity;

import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.chunk.GlowChunk.Key;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.jetbrains.annotations.NonNls;

/**
 * Base class for block entities (blocks with NBT data) in the world. Most access to block entities
 * should occur through the Bukkit BlockState API.
 */
public abstract class BlockEntity {

    /**
     * Get the block this BlockEntity is associated with.
     *
     * @return The entity's block.
     */
    @Getter
    protected final GlowBlock block;
    private String saveId;

    /**
     * Create a new BlockEntity at the given location.
     *
     * @param block The block the BlockEntity is attached to.
     */
    public BlockEntity(GlowBlock block) {
        this.block = block;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility stuff

    /**
     * Update this BlockEntity's visible state to all players in range.
     */
    public final void updateInRange() {
        Key key = GlowChunk.Key.of(block.getX() >> 4, block.getZ() >> 4);
        block.getWorld().getRawPlayers().stream().filter(player -> player.canSeeChunk(key))
            .forEach(this::update);
    }

    ////////////////////////////////////////////////////////////////////////////
    // World I/O

    /**
     * Set the text ID this block entity is saved to disk with. If this is not set, then load and
     * save of the "id" tag must be performed manually.
     *
     * @param saveId The ID.
     */
    protected final void setSaveId(@NonNls String saveId) {
        if (this.saveId != null) {
            throw new IllegalStateException("Can only set saveId once");
        }
        this.saveId = saveId;
    }

    /**
     * Read this BlockEntity's data from the saved tag.
     *
     * @param tag The tag to load from.
     */
    public void loadNbt(CompoundTag tag) {
        // verify id and coordinates
        if (saveId != null) {
            if (!tag.isString("id") || !tag.getString("id").equals(saveId)) {
                throw new IllegalArgumentException(
                    "Expected block entity id of " + saveId + ", got " + tag.getString("id"));
            }
        }

        // verify coordinates if provided
        if (tag.isInt("x")) {
            int x = tag.getInt("x");
            int y = tag.getInt("y");
            int z = tag.getInt("z");
            int rx = block.getX();
            int ry = block.getY();
            int rz = block.getZ();
            if (x != rx || y != ry || z != rz) {
                throw new IllegalArgumentException(
                    "Tried to load block entity with coords (" + x + "," + y + "," + z + ") into ("
                        + rx + "," + ry + "," + rz + ")");
            }
        }
    }

    /**
     * Save this BlockEntity's data to NBT.
     *
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
     * Create a new BlockState which will correspond to this block entity.
     *
     * @return A GlowBlockState, or null to use a standard BlockState.
     */
    public GlowBlockState getState() {
        return null;
    }

    /**
     * Destroy this BlockEntity.
     */
    public void destroy() {
        // nothing by default
    }

    /**
     * Update this BlockEntity's visible state to the given player.
     *
     * @param player The player to update.
     */
    public void update(GlowPlayer player) {
        // nothing by default
    }

    /**
     * Generic pulse for this block entity.
     */
    public void pulse() {
        // nothing by default
    }
}
