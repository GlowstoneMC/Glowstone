package net.glowstone.inventory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.glowstone.constants.ItemIds;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Tracker for when items in an inventory are changed.
 */
public final class InventoryMonitor {

    private static int nextId = 1;

    private final InventoryView view;
    private final ItemStack[] slots;
    /**
     * Get the number of slots in this inventory view.
     *
     * @return The number of slots.
     */
    @Getter
    private final int size;
    /**
     * The network ID of this inventory view.
     *
     * @return The id.
     */
    @Getter
    private final int id;

    /**
     * Get the network type ID of this inventory view.
     *
     * @return The type id.
     */
    @Getter
    private final String type;

    /**
     * Create a new monitor for the given inventory view.
     *
     * @param view The view to monitor.
     */
    public InventoryMonitor(InventoryView view) {
        this.view = view;
        if (view.getTopInventory().getType() != InventoryType.CRAFTING
            && view.getBottomInventory().getType() == InventoryType.PLAYER) {
            // Don't send armor/shield slots when looking in an inventory
            size = view.countSlots() - 5;
        } else {
            size = view.countSlots();
        }
        slots = new ItemStack[size];

        // determine id and type id
        if (GlowInventoryView.isDefault(view)) {
            id = 0;
        } else {
            id = nextId;
            nextId = nextId % 100 + 1;
        }
        type = getTypeId(view.getType());

        // set initial contents
        for (int i = 0; i < size; ++i) {
            updateItem(i);
        }
    }

    /**
     * Get the network ID for the given inventory type.
     *
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

    /**
     * Update the given slot with the current value from the view.
     *
     * @param slot The slot to update.
     */
    private void updateItem(int slot) {
        // sanitize() used as a last line of defense to prevent client crashes
        // GlowInventory should generally be able to keep its contents safe
        ItemStack source = view.getItem(slot);
        slots[slot] = source == null ? null : ItemIds.sanitize(source.clone());
    }

    /**
     * Check for changes in the inventory view.
     *
     * @return The list of changed items.
     */
    public List<Entry> getChanges() {
        List<Entry> result = new LinkedList<>();
        // reverse to support crafting table.
        for (int i = size - 1; i >= 0; --i) {
            if (!Objects.equals(slots[i], view.getItem(i))) {
                updateItem(i);
                result.add(new Entry(i, slots[i]));
            }
        }
        return result;
    }

    /**
     * Get the current contents of the viewed inventory.
     *
     * @return The contents.
     */
    public ItemStack[] getContents() {
        // TODO: Defensive deep copy
        return slots;
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
}
