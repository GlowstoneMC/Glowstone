package net.glowstone.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import net.glowstone.util.collection.SuperIterator;
import net.glowstone.util.collection.SuperSet;

import java.util.Iterator;
import java.util.List;

/**
 * Inventory which delegate to other Inventory objects.
 */
public class GlowSuperInventory extends GlowBaseInventory {
    private List<GlowBaseInventory> parents;

    protected GlowSuperInventory() { }

    public GlowSuperInventory(InventoryHolder owner, InventoryType type, List<GlowBaseInventory> parents) {
        initialize(owner, type, parents);
    }

    public GlowSuperInventory(InventoryHolder owner, InventoryType type, String title, List<GlowBaseInventory> parents) {
        initialize(owner, type, title, parents);
    }

    // Why these weird initialize methods, instead of handling everything
    // in the constructors?
    //
    // Well, the subclass GlowDoubleChestInventory owner must be a
    // DoubleChest. However, a DoubleChest takes a DoubleChestInventory as the
    // only constructor parameter. However, using "this" inside a super call
    // isn't possible (Java compiler complains), so stuff like
    // "super(new DoubleChest(this), ...)" can't be used.
    //
    // Using "this" after the super call inside the constructor is possible,
    // so I'm using these pseudo-constructors.
    protected void initialize(InventoryHolder owner, InventoryType type, List<GlowBaseInventory> parents) {
        initialize(owner, type, type.getDefaultTitle(), parents);
    }
    
    protected void initialize(InventoryHolder owner, InventoryType type, String title, List<GlowBaseInventory> parents) {
        super.initialize(owner, type, title, new SuperSet<HumanEntity>());
        this.parents = parents;

        for (GlowBaseInventory parent : parents) {
            ((SuperSet<HumanEntity>) getViewersSet()).getParents().add(parent.getViewersSet());
        }
    }

    @Override
    public GlowInventorySlot getSlot(int slot) {
        int relativeSlot = slot;

        for (GlowBaseInventory parent : parents) {
            int parentSize = parent.getSize();

            if (relativeSlot < parentSize) {
                return parent.getSlot(relativeSlot);
            }

            relativeSlot -= parentSize;
        }

        throw new IndexOutOfBoundsException("Inventory does not contain slot " + slot);
    }

    @Override
    public int getSize() {
        int size = 0;

        for (GlowBaseInventory parent : parents) {
            size += parent.getSize();
        }

        return size;
    }

    @Override
    public Iterator<GlowInventorySlot> slotIterator() {
        return new SuperIterator<GlowInventorySlot>(parents, InventorySlotIteratorAdapter.INSTANCE);
    }

    public List<GlowBaseInventory> getParents() {
        return parents;
    }
}
