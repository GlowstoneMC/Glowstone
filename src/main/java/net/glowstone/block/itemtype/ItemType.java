package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Base class for specific types of items.
 */
public class ItemType {

    private int id = -1;

    private BlockType placeAs;

    /**
     * The maximum stack size of the item.
     */
    private int maxStackSize = 64;

    /**
     * Assign an id number to this ItemType (for internal use only).
     */
    public final void setId(int id) {
        if (this.id != -1) {
            throw new IllegalStateException("Id is already set in " + this);
        }
        this.id = id;

        // pull a few defaults from Material if possible
        Material mat = getMaterial();
        if (mat != null) {
            maxStackSize = mat.getMaxStackSize();
        }
    }

    /**
     * Get the id assigned to this ItemType.
     * @return The corresponding id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the Material assigned to this ItemType.
     * @return The corresponding Material.
     */
    public final Material getMaterial() {
        return Material.getMaterial(getId());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Setters for subclass use

    /**
     * Set the maximum stack size of the item.
     * @param maxStackSize The new maximum stack size.
     */
    protected final void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    /**
     * Set this item to act like the given block type when being placed.
     * @param placeAs The block to place as.
     */
    protected final void setPlaceAs(BlockType placeAs) {
        this.placeAs = placeAs;
    }

    /**
     * Set this item to act like the given block type when being placed.
     * @param placeAs The material to place as.
     */
    protected final void setPlaceAs(Material placeAs) {
        if (placeAs == null) {
            this.placeAs = null;
        } else {
            this.placeAs = ItemTable.instance().getBlock(placeAs);
            if (this.placeAs == null) {
                throw new IllegalArgumentException("Material " + placeAs + " is not a valid block");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    /**
     * Get the maximum stack size of the item.
     * @return The maximum stack size.
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    /**
     * Called when a player right-clicks in midair while holding this item.
     * Also called by default if rightClickBlock is not overridden.
     * @param player The player
     * @param holding The ItemStack the player was holding
     */
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        // nothing by default
    }

    /**
     * Called when a player right-clicks on a block while holding this item.
     * @param player The player
     * @param target The block the player right-clicked
     * @param face The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @param clickedLoc The coordinates at which the click occurred
     */
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc) {
        if (placeAs != null) {
            placeAs.rightClickBlock(player, target, face, holding, clickedLoc);
        } else {
            rightClickAir(player, holding);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility stuff

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "{" + getId() + " -> " + getMaterial() + "}";
    }
}
