package net.glowstone.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;

/**
 * ListIterator for the contents of an inventory.
 */
public class InventoryIterator implements ListIterator<ItemStack> {

    private final Inventory inventory;
    private int nextIndex;
    private byte direction;  // -1: backwards, 0: not moved, 1: forwards

    public InventoryIterator(Inventory inventory) {
        this.inventory = inventory;
        this.nextIndex = 0;
    }

    public InventoryIterator(Inventory inventory, int position) {
        this.inventory = inventory;
        this.nextIndex = position;
    }

    public boolean hasNext() {
        return nextIndex < inventory.getSize();
    }

    public ItemStack next() {
        direction = 1;
        return inventory.getItem(nextIndex++);
    }

    public boolean hasPrevious() {
        return nextIndex > 0;
    }

    public ItemStack previous() {
        direction = -1;
        return inventory.getItem(--nextIndex);
    }

    public int nextIndex() {
        return nextIndex;
    }

    public int previousIndex() {
        return nextIndex - 1;
    }

    public void set(ItemStack itemStack) {
        if (direction == 0) {
            throw new IllegalStateException("Must call next or previous first");
        }
        int i = direction > 0 ? nextIndex - 1 : nextIndex;
        inventory.setItem(i, itemStack);
    }

    public void add(ItemStack itemStack) {
        throw new UnsupportedOperationException("Cannot add or remove from inventory");
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot add or remove from inventory");
    }
}
