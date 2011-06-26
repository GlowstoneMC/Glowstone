package net.glowstone.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Slot;

/**
 * A class which represents an {@link ItemStack} and its associated slot in an
 * inventory.
 */
public final class GlowSlot implements Slot {

    /**
     * The inventory.
     */
    private final Inventory inventory;

    /**
     * The slot.
     */
    private final int slot;

    /**
     * The item.
     */
    private final ItemStack item;

    /**
     * Creates a slotted item.
     * @param slot The slot.
     * @param item The item.
     */
    public GlowSlot(Inventory inventory, int slot, ItemStack item) {
        this.inventory = inventory;
        this.slot = slot;
        this.item = item;
    }

    /*
     * Gets the inventory.
     * @return The inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets the slot index.
     * @return The slot index.
     */
    public int getIndex() {
        return slot;
    }

    /**
     * Gets the item.
     * @return The item.
     */
    public ItemStack getItem() {
        return item;
    }

}
