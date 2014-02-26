package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.itemtype.ItemType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

/**
 * Base class for specific types of blocks.
 */
public class BlockType extends ItemType {

    /**
     * Create a new tile entity at the given location.
     *
     * @param block The block to create the tile entity at.
     * @return The new GlowBlockState, or null if no tile entity is used.
     */
    public GlowBlockState createTileEntity(GlowBlock block) {
        return null;
    }

    /**
     * Check whether the block can be placed at the given location.
     *
     * @param block The location the block is being placed at.
     * @param against The face the block is being placed against.
     * @return Whether the placement is valid.
     */
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return true;
    }

    /**
     * Get the items that will be dropped by digging the block.
     *
     * @param block The block being dug.
     * @return The drops that should be returned.
     */
    public Collection<ItemStack> getDrops(GlowBlock block) {
        return Arrays.asList();
    }

}
