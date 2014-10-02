package net.glowstone.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Tracker for when items in an inventory are changed.
 */
public final class InventoryMonitor {

    private static int nextId = 1;

    private final InventoryView view;
    private final ItemStack[] slots;
    private final int size, id;
    private final String type;

    /**
     * Create a new monitor for the given inventory view.
     * @param view The view to monitor.
     */
    public InventoryMonitor(InventoryView view) {
        boolean isDefault = GlowInventoryView.isDefault(view);
        this.view = view;
        this.size = view.countSlots() + (isDefault ? 4 : 0);
        this.slots = new ItemStack[size];

        // determine id and type id
        if (isDefault) {
            id = 0;
        } else {
            id = nextId;
            nextId = (nextId % 100) + 1;
        }
        type = getTypeId(view.getType());

        // set initial contents
        for (int i = 0; i < size; ++i) {
            updateItem(i);
        }
    }

    /**
     * Update the given slot with the current value from the view.
     * @param slot The slot to update.
     */
    private void updateItem(int slot) {
        ItemStack source = view.getItem(slot);
        slots[slot] = source == null ? null : source.clone();
    }

    /**
     * Check for changes in the inventory view.
     * @return The list of changed items.
     */
    public List<Entry> getChanges() {
        List<Entry> result = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            if (!Objects.equals(slots[i], view.getItem(i))) {
                updateItem(i);
                result.add(new Entry(i, slots[i]));
            }
        }
        return result;
    }

    /**
     * Get the current contents of the viewed inventory.
     * @return The contents.
     */
    public ItemStack[] getContents() {
        return slots;
    }

    /**
     * Get the number of slots in this inventory view.
     * @return The number of slots.
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the network ID of this inventory view.
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the network type ID of this inventory view.
     * @return The type id.
     */
    public String getType() {
        return type;
    }

    /**
     * An entry which has been changed.
     */
    public static class Entry {
        public final int slot;
        public final ItemStack item;

        public Entry(int slot, ItemStack item) {
            this.slot = slot;
            this.item = item;
        }
    }

    /**
     * Get the network ID for the given inventory type.
     * @param type The type.
     * @return The id.
     */
    private static String getTypeId(InventoryType type) {
        switch (type) {
            case WORKBENCH:
                return "minecraft:crafting_table";
            case FURNACE:
                return "minecraft:furnace";
            case DISPENSER:
                return "minecraft:dispenser";
            case ENCHANTING:
                return "minecraft:enchanting_table";
            case BREWING:
                return "minecraft:brewing_stand";
            case MERCHANT:
                return "minecraft:villager";
            case BEACON:
                return "minecraft:beacon";
            case ANVIL:
                return "minecraft:anvil";
            case HOPPER:
                return "minecraft:hopper";
            case DROPPER:
                return "minecraft:dropper";
            case PLAYER:
            case CHEST:
            case ENDER_CHEST:
            default:
                return "minecraft:chest";
        }
    }
}
