package net.glowstone.inventory;

import net.glowstone.util.Adapter;

import java.util.Iterator;

/**
 * Adapter which calls GlowBaseInventory slotIterator.
 */
public class InventorySlotIteratorAdapter implements Adapter<GlowBaseInventory, Iterator> {

    @Override
    public Iterator adapt(GlowBaseInventory inventory) {
        return inventory.slotIterator();
    }

    public static final InventorySlotIteratorAdapter INSTANCE = new InventorySlotIteratorAdapter();
}
