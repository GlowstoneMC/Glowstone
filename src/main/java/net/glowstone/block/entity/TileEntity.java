package net.glowstone.block.entity;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for tile entities (blocks with NBT data) in the world.
 * Most access to tile entities should occur through the Bukkit BlockState API.
 */
public class TileEntity {

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
            if (player.canSee(key)) {
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
            if (!tag.is("id", StringTag.class) || !tag.get("id", StringTag.class).equals(saveId)) {
                throw new IllegalArgumentException("Expected tile entity id of " + saveId + ", got " + tag.get("id", StringTag.class));
            }
        }

        // verify coordinates if provided
        if (tag.is("x", IntTag.class)) {
            int x = tag.get("x", IntTag.class);
            int y = tag.get("y", IntTag.class);
            int z = tag.get("z", IntTag.class);
            int rx = block.getX(), ry = block.getY(), rz = block.getZ();
            if (x != rx || y != ry || z != rz) {
                throw new IllegalArgumentException("Tried to load tile entity with coords (" + x + "," + y + "," + z + ") into (" + rx + "," + ry + "," + rz + ")");
            }
        }
    }

    /**
     * Save this TileEntity's data to NBT.
     * @return The tag list.
     */
    public List<Tag> saveNbt() {
        List<Tag> result = new ArrayList<>();
        if (saveId != null) {
            result.add(new StringTag("id", saveId));
        }
        result.add(new IntTag("x", block.getX()));
        result.add(new IntTag("y", block.getY()));
        result.add(new IntTag("z", block.getZ()));
        return result;
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
