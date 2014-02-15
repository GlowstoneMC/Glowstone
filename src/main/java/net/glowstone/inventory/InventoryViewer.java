package net.glowstone.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an entity which can view an inventory.
 */
public interface InventoryViewer {

    /**
     * Inform the viewer that an item has changed.
     * @param inventory The GlowInventory in which a slot has changed.
     * @param slot      The slot number which has changed.
     * @param item      The ItemStack which the slot has changed to.
     */
    void onSlotSet(GlowInventory inventory, int slot, ItemStack item);

    /**
     * Inform the viewer that the whole inventory's contents have changed.
     * @param inventory The GlowInventory which has changed.
     * @param slots     The new contents of the inventory.
     */
    void onContentsSet(GlowInventory inventory, ItemStack[] slots);

}
