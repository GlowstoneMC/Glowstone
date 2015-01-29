package net.glowstone.block.block2;

import net.glowstone.GlowChunk;
import net.glowstone.block.*;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Behavior applicable to a block type.
 */
public interface BlockBehavior {

    /**
     * Get the items that will be dropped by digging the block.
     * @param block The block being dug.
     * @param tool The tool used or {@code null} if fists or no tool was used.
     * @return The drops that should be returned.
     */
    Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool);

    /**
     * Create a new tile entity at the given location.
     * @param chunk The chunk to create the tile entity at.
     * @param cx The x coordinate in the chunk.
     * @param cy The y coordinate in the chunk.
     * @param cz The z coordinate in the chunk.
     * @return The new TileEntity, or null if no tile entity is used.
     */
    TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz);

    /**
     * Check whether the block can be placed at the given location.
     * @param block The location the block is being placed at.
     * @param against The face the block is being placed against.
     * @return Whether the placement is valid.
     */
    boolean canPlaceAt(GlowBlock block, BlockFace against);

    /**
     * Called when a block is placed to calculate what the block will become.
     * @param player the player who placed the block
     * @param state the BlockState to edit
     * @param holding the ItemStack that was being held
     * @param face the face off which the block is being placed
     * @param clickedLoc where in the block the click occurred
     */
    void placeBlock(GlowPlayer player, net.glowstone.block.GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc);

    /**
     * Called after a block has been placed by a player.
     * @param player the player who placed the block
     * @param block the block that was placed
     * @param holding the the ItemStack that was being held
     */
    void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding);

    /**
     * Called when a player attempts to interact with (right-click) a block of
     * this type already in the world.
     * @param player the player interacting
     * @param block the block interacted with
     * @param face the clicked face
     * @param clickedLoc where in the block the click occurred
     * @return Whether the interaction occurred.
     */
    boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc);

    /**
     * Called when a player attempts to destroy a block.
     * @param player The player interacting
     * @param block The block the player destroyed
     * @param face The block face
     */
    void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face);

    /**
     * Called when a player attempts to place a block on an existing block of
     * this type. Used to determine if the placement should occur into the air
     * adjacent to the block (normal behavior), or absorbed into the block
     * clicked on.
     * @param block The block the player right-clicked
     * @param face The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @return Whether the place should occur into the block given.
     */
    boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding);

    /**
     * Called to check if this block can be overridden by a block place
     * which would occur inside it.
     * @param block The block being targeted by the placement
     * @param face The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @return Whether this block can be overridden.
     */
    boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding);

    /**
     * Called when a neighboring block (within a 3x3x3 cube) has changed its
     * type or data and physics checks should occur.
     * @param block The block to perform physics checks for
     * @param face The BlockFace to the changed block, or null if unavailable
     * @param changedBlock The neighboring block that has changed
     * @param oldType The old type of the changed block
     * @param oldData The old data of the changed block
     * @param newType The new type of the changed block
     * @param newData The new data of the changed block
     */
    void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData);

    /**
     * Called when this block has just changed to some other type. This is
     * called whenever {@link GlowBlock#setTypeIdAndData}, {@link GlowBlock#setType}
     * or {@link GlowBlock#setData} is called with physics enabled, and might
     * be called from plugins or other means of changing the block.
     * @param block The block that was changed
     * @param oldType The old Material
     * @param oldData The old data
     * @param newType The new Material
     * @param data The new data
     */
    void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType, byte data);

    /**
     * Called when the BlockType should calculate the current physics.
     * @param me The block
     */
    void updatePhysics(GlowBlock me);
}
