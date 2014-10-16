package net.glowstone.inventory;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Tracker for when the equipment of an entity is changed.
 */
public final class EquipmentMonitor {

    /**
     * The entity whose equipment is being monitored.
     */
    private final LivingEntity entity;

    /**
     * The previous equipment.
     */
    private final ItemStack[] slots = new ItemStack[5];

    /**
     * All changes between the previous equipment.
     */
    private final List<Entry> changes = new LinkedList<>();

    /**
     * Whether the {@link #changes} have been calculated for this tick.
     */
    private boolean changesCalculated;

    /**
     * Create a new monitor for the given entity.
     * @param entity The entity whose equipment to monitor.
     */
    public EquipmentMonitor(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * Get the item in the inventory.
     * Slot 0 is the item in the hand.
     * Slot 1 to 4 is armor (boots to helmet).
     * @return The item in that slot.
     */
    private ItemStack getItem(int slot) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) {
            return null;
        }
        if (slot == 0) {
            return equipment.getItemInHand();
        } else {
            return equipment.getArmorContents()[slot - 1];
        }
    }

    /**
     * Update the given slot with the current value from the inventory.
     * @param slot The slot to update.
     */
    private void updateItem(int slot) {
        ItemStack source = getItem(slot);
        slots[slot] = source == null ? null : source.clone();
    }

    /**
     * Check for changes in the inventory view.
     * @return The list of changed items.
     */
    public List<Entry> getChanges() {
        if (!changesCalculated) {
            for (int i = 0; i < 5; ++i) {
                ItemStack item = getItem(i);
                if (!Objects.equals(slots[i], item)) {
                    changes.add(new Entry(i, item));
                }
            }
            changesCalculated = true;
        }
        return changes;
    }

    /**
     * Reset all cached changes and update latest content.
     */
    public void resetChanges() {
        changes.clear();
        changesCalculated = false;
        for (int i = 0; i < 5; i++) {
            updateItem(i);
        }
    }

    /**
     * Get the entity whose equipment is being monitored.
     * @return The entity equipment.
     */
    public LivingEntity getEntity() {
        return entity;
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
