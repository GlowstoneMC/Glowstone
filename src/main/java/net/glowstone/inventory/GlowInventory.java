package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

/**
 * A class which stores items in a list.
 */
public class GlowInventory extends GlowBaseInventory {

    /**
     * This inventory's slots.
     */
    private List<GlowInventorySlot> slots;

    public GlowInventory(InventoryHolder owner, InventoryType type) {
        this(owner, type, type.getDefaultSize(), type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder owner, InventoryType type, int size) {
        this(owner, type, size, type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder owner, InventoryType type, int size, String title) {
        initialize(owner, type, title, new HashSet<HumanEntity>());

        this.slots = GlowInventorySlot.createList(size);
    }

    @Override
    public GlowInventorySlot getSlot(int slot) {
        return slots.get(slot);
    }

    /**
     * Get the type of the specified slot.
     * @param slot The slot number.
     * @return The SlotType of the slot.
     */
    public SlotType getSlotType(int slot) {
        if (slot < 0) return SlotType.OUTSIDE;
        return getSlot(slot).getType();
    }

    /**
     * Check whether it is allowed for a player to insert the given ItemStack
     * at the slot, regardless of the slot's current contents. Should return
     * false for crafting output slots or armor slots which cannot accept
     * the given item.
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemPlaceAllowed(int slot, ItemStack stack) {
        return getSlotType(slot) != SlotType.RESULT;
    }

    /**
     * Check whether, in a shift-click operation, an item of the specified type
     * may be placed in the given slot.
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemShiftClickAllowed(int slot, ItemStack stack) {
        return itemPlaceAllowed(slot, stack);
    }

    /**
     * Set the custom title of this inventory or reset it to the default.
     * @param title The new title, or null to reset.
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = type.getDefaultTitle();
        } else {
            this.title = title;
        }
    }

    /**
     * Gets the number of slots in this inventory according to the protocol.
     * Some inventories have 0 slots in the protocol, despite having slots.
     * @return The numbers of slots
     */
    public int getRawSlots() {
        return getSize();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    @Override
    public int getSize() {
        return slots.size();
    }

    @Override
    public Iterator<GlowInventorySlot> slotIterator() {
        return slots.iterator();
    }
}
