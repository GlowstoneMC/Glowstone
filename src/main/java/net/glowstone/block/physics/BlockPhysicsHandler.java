package net.glowstone.block.physics;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.block.BlockFace;

public interface BlockPhysicsHandler {
    public boolean canPlaceAt(GlowBlock block, BlockFace against);

    /**
     * Perform physics on a block from a direct update.
     * @param block The block that changed.
     * @return Whether this physics update changed anything.
     */
    public boolean doPhysics(GlowBlock block);

    /**
     * Perform physics on this block after a change from a neighboring block.
     * @param block The block that changed
     * @param against The neighbor that triggered this update.
     * @return Whether this physics update changed anything.
     */
    public boolean postUpdateNeighbor(GlowBlock block, BlockFace against);

    /**
     * Returns the metadata that should be returned
     *
     * @param current The metadata of the item stack that was used to place this or 0
     * @param against The block face this was placed against or SELF if 
     * @return The metadata that should be placed.
     */
    public int getPlacedMetadata(int current, BlockFace against);

    /**
     * Performs a special action when placing the block.
     * @param block The block where a normal placement would occur.
     * @param type The type of the player's current item in hand.
     * @param data The data of the player's current item in hand.
     * @param against The blockface that this placement action occured against
     * @return Whether to place the block normally or just subtract an item.
     */
    public GlowBlockState placeAgainst(GlowBlockState block, int type, short data, BlockFace against);
}
