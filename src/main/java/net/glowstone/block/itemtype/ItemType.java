package net.glowstone.block.itemtype;

import com.google.common.base.Preconditions;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Base class for specific types of items.
 */
public class ItemType {

    private int id = -1;
    private Material material;

    private BlockType placeAs;

    /**
     * The maximum stack size of the item.
     */
    private int maxStackSize = 64;

    /**
     * Get the id assigned to this ItemType.
     *
     * @return The corresponding id.
     * @deprecated Magic value
     */
    @Deprecated
    public final int getId() {
        return material == null ? id : material.getId();
    }

    /**
     * Assign an item ID to this ItemType (for internal use only).
     *
     * @param id The internal item id for this item.
     * @deprecated Magic value
     */
    @Deprecated
    public final void setId(int id) {
        if (material != null && this.id != -1) {
            throw new IllegalStateException("ID is already set in " + this);
        }
        Material mat = Material.getMaterial(id);

        if (mat == null) {
            this.id = id;
        } else {
            setMaterial(mat);
        }
    }

    /**
     * Get the Material assigned to this ItemType.
     *
     * @return The corresponding Material.
     */
    public final Material getMaterial() {
        return material;
    }

    /**
     * Assign a Material to this ItemType (for internal use only).
     *
     * @param material The internal material for this item.
     */
    public final void setMaterial(Material material) {
        if (this.material != null && id != -1) {
            throw new IllegalStateException("Material is already set in " + this);
        }
        Preconditions.checkNotNull(material);
        this.material = material;
        id = material.getId();
        maxStackSize = material.getMaxStackSize();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Setters for subclass use

    /**
     * Set this item to act like the given block type when being placed.
     *
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

    /**
     * Set this item to act like the given block type when being placed.
     *
     * @param placeAs The block to place as.
     */
    protected final void setPlaceAs(BlockType placeAs) {
        this.placeAs = placeAs;
    }

    /**
     * The type of block to place when the item is used.
     *
     * @return the type of block to place
     */
    public BlockType getPlaceAs() {
        return placeAs;
    }

    /**
     * Get the maximum stack size of the item.
     *
     * @return The maximum stack size.
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    /**
     * Set the maximum stack size of the item.
     *
     * @param maxStackSize The new maximum stack size.
     */
    protected final void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    /**
     * Called when a player right-clicks in midair while holding this item. Also called by default
     * if rightClickBlock is not overridden.
     *
     * @param player The player
     * @param holding The ItemStack the player was holding
     */
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        // nothing by default
    }

    /**
     * Get the context this item can be used in.
     *
     * @return context of the item, default is {{@link Context#BLOCK}}
     */
    public Context getContext() {
        return Context.BLOCK;
    }

    /**
     * Called when a player right-clicks on a block while holding this item.
     *
     * @param player The player
     * @param target The block the player right-clicked
     * @param face The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @param clickedLoc The coordinates at which the click occurred
     */
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (placeAs != null) {
            Context context = placeAs.getContext();
            if ((context == Context.ANY || context == Context.BLOCK)) {
                placeAs.rightClickBlock(player, target, face, holding, clickedLoc, hand);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility stuff

    @Override
    public final String toString() {
        return getClass().getSimpleName()
                + "{" + (getMaterial() == null ? getId() : getMaterial())  + "}";
    }

    /**
     * Context of the Items interaction.
     */
    public enum Context {
        /**
         * The item can only be used when clicking in the air.
         */
        AIR,
        /**
         * The item can only be used when clicking against a block.
         */
        BLOCK,
        /**
         * The item can be used on any click.
         */
        ANY
    }
}
