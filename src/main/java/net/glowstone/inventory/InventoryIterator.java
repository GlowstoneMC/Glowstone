package net.glowstone.inventory;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * ListIterator for the contents of an inventory.
 */
public final class InventoryIterator implements ListIterator<ItemStack> {

    private final Inventory inventory;
    private int nextIndex;
    private byte direction;  // -1: backwards, 0: not moved, 1: forwards

    public InventoryIterator(Inventory inventory) {
        this.inventory = inventory;
        nextIndex = 0;
    }

    public InventoryIterator(Inventory inventory, int position) {
        this.inventory = inventory;
        nextIndex = position;
    }

    @Override
    public boolean hasNext() {
        return nextIndex < inventory.getSize();
    }

    @Override
    public ItemStack next() {
        direction = 1;
        try {
            return inventory.getItem(nextIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean hasPrevious() {
        return nextIndex > 0;
    }

    @Override
    public ItemStack previous() {
        direction = -1;
        return inventory.getItem(--nextIndex);
    }

    @Override
    public int nextIndex() {
        return nextIndex;
    }

    @Override
    public int previousIndex() {
        return nextIndex - 1;
    }

    @Override
    public void set(ItemStack itemStack) {
        if (direction == 0) {
            throw new IllegalStateException("Must call next or previous first");
        }
        int i = direction > 0 ? nextIndex - 1 : nextIndex;
        inventory.setItem(i, itemStack);
    }

    @Override
    public void add(ItemStack itemStack) {
        throw new UnsupportedOperationException("Cannot add or remove from inventory");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot add or remove from inventory");
    }
}
