package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * An abstract class which represents an inventory.
 */
public abstract class GlowBaseInventory implements Inventory {

    /**
     * The list of humans viewing this inventory.
     */
    private Set<HumanEntity> viewers;

    /**
     * The owner of this inventory.
     */
    private InventoryHolder owner;

    /**
     * The type of this inventory.
     */
    private InventoryType type;

    /**
     * The inventory's name.
     */
    private String title;

    /**
     * The inventory's maximum stack size.
     */
    private int maxStackSize = 64;

    protected GlowBaseInventory() { }

    /**
     * Initializes some key components of this inventory. This should be called in the constructor.
     * @param owner InventoryHolder which owns this Inventory.
     * @param type The inventory type.
     * @param title Inventory title, displayed in the client.
     * @param viewers Set for storage of current inventory viewers.
     */
    protected void initialize(InventoryHolder owner, InventoryType type, String title, Set<HumanEntity> viewers) {
        this.owner = owner;
        this.type = type;
        this.title = title;
        this.viewers = viewers;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Abstract methods

    /**
     * Returns a certain slot.
     * @param Slot index.
     * @return The requested slot.
     */
    public abstract GlowInventorySlot getSlot(int slot);

    public abstract int getSize();

    /**
     * Returns a slot iterator for efficient linear navigation.
     * @return Slot iterator.
     */
    public abstract Iterator<GlowInventorySlot> slotIterator();

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Add a viewer to the inventory.
     * @param viewer The HumanEntity to add.
     */
    public void addViewer(HumanEntity viewer) {
        viewers.add(viewer);
    }

    /**
     * Remove a viewer from the inventory.
     * @param viewer The HumanEntity to remove.
     */
    public void removeViewer(HumanEntity viewer) {
        viewers.remove(viewer);
    }

    /**
     * Returns the set which contains viewers.
     * @return Viewers set.
     */
    public Set<HumanEntity> getViewersSet() {
        return viewers;
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

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    @Override
    public final InventoryType getType() {
        return type;
    }

    @Override
    public InventoryHolder getHolder() {
        return owner;
    }

    @Override
    public final String getName() {
        return title;
    }

    @Override
    public final String getTitle() {
        return title;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
    }

    @Override
    public List<HumanEntity> getViewers() {
        return new ArrayList<>(viewers);
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            // negative indices go from back
            index += getSize() + 1;
        }
        return new InventoryIterator(this, index);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get, Set, Add, Remove

    @Override
    public ItemStack getItem(int index) {
        ItemStack item = getSlot(index).getItem();
        if (item != null) {
            // Defensive copy
            item = item.clone();
        }
        return item;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (item != null) {
            // Defensive copy
            item = item.clone();
        }
        getSlot(index).setItem(item);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            ItemStack remaining = addItemStack(items[i], true);

            if (remaining != null) {
                result.put(i, remaining);
            }
        }

        return result;
    }

    public ItemStack addItemStack(ItemStack item, boolean ignoreMeta) {
        int maxStackSize = item.getType() == null ? 64 : item.getType().getMaxStackSize();
        int toAdd = item.getAmount();

        for (int i = 0; toAdd > 0 && i < getSize(); i++) {
            // Look for existing stacks to add to
            ItemStack slotItem = getItem(i);
            if (slotItem != null && compareItems(item, slotItem, ignoreMeta)) {
                int space = maxStackSize - slotItem.getAmount();
                if (space < 0) continue;
                if (space > toAdd) space = toAdd;

                slotItem.setAmount(slotItem.getAmount() + space);
                setItem(i, slotItem);

                toAdd -= space;
            }
        }

        if (toAdd > 0) {
            // Look for empty slots to add to
            for (int i = 0; toAdd > 0 && i < getSize(); i++) {
                ItemStack slotItem = getItem(i);
                if (slotItem == null) {
                    int num = toAdd > maxStackSize ? maxStackSize : toAdd;

                    slotItem = new ItemStack(item.getType(), num);
                    setItem(i, slotItem);

                    toAdd -= num;
                }
            }
        }

        if (toAdd > 0) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(toAdd);
            return remaining;
        }

        return null;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            ItemStack remaining = removeItemStack(items[i], true);

            if (remaining != null) {
                result.put(i, remaining);
            }
        }

        return result;
    }

    public ItemStack removeItemStack(ItemStack item, boolean ignoreMeta) {
        int toRemove = item.getAmount();

        for (int i = 0; toRemove > 0 && i < getSize(); i++) {
            ItemStack slotItem = getItem(i);
            // Look for stacks to remove from.
            if (compareItems(item, slotItem, ignoreMeta)) {
                if (slotItem.getAmount() > toRemove) {
                    slotItem.setAmount(slotItem.getAmount() - toRemove);
                    setItem(i, slotItem);
                } else {
                    toRemove -= slotItem.getAmount();
                    slotItem = null;
                }
            }
        }

        if (toRemove > 0) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(toRemove);
            return remaining;
        }

        return null;
    }

    private boolean compareItems(ItemStack a, ItemStack b, boolean ignoreMeta) {
        if (ignoreMeta) {
            return a.getTypeId() == b.getTypeId() && a.getDurability() == b.getDurability();
        }

        return a.isSimilar(b);
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];

        for (int i = 0; i < getSize(); i++) {
            contents[i] = getItem(i);
        }

        return contents;
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (items.length != getSize()) {
            throw new IllegalArgumentException("Length of items must be " + getSize());
        }

        for (int i = 0; i < getSize(); i++) {
            setItem(i, items[i]);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Contains

    @Override
    public boolean contains(int materialId) {
        return first(materialId) >= 0;
    }

    @Override
    public boolean contains(Material material) {
        return first(material) >= 0;
    }

    @Override
    public boolean contains(ItemStack item) {
        return first(item) >= 0;
    }

    @Override
    public boolean contains(int materialId, int amount) {
        HashMap<Integer, ? extends ItemStack> found = all(materialId);
        int total = 0;
        for (ItemStack stack : found.values()) {
            total += stack.getAmount();
        }
        return total >= amount;
    }

    @Override
    public boolean contains(Material material, int amount) {
        return contains(material.getId(), amount);
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return contains(item.getTypeId(), amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return false;  // todo
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find all

    @Override
    public HashMap<Integer, ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> result = new HashMap<>();
        for (int i = 0; i < getSize(); ++i) {
            ItemStack slotItem = getItem(i);
            if (slotItem.getTypeId() == materialId) {
                result.put(i, slotItem);
            }
        }
        return result;
    }

    @Override
    public HashMap<Integer, ItemStack> all(Material material) {
        return all(material.getId());
    }

    @Override
    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> result = new HashMap<>();
        for (int i = 0; i < getSize(); ++i) {
            ItemStack slotItem = getItem(i);
            if (slotItem != null && slotItem.equals(item)) {
                result.put(i, slotItem);
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find first

    @Override
    public int first(int materialId) {
        for (int i = 0; i < getSize(); ++i) {
            ItemStack slotItem = getItem(i);
            if (slotItem != null && slotItem.getTypeId() == materialId) return i;
        }
        return -1;
    }

    @Override
    public int first(Material material) {
        return first(material.getId());
    }

    @Override
    public int first(ItemStack item) {
        for (int i = 0; i < getSize(); ++i) {
            ItemStack slotItem = getItem(i);
            if (slotItem != null && slotItem.equals(item)) return i;
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < getSize(); ++i) {
            if (getItem(i) == null) return i;
        }
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Remove

    @Override
    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        for (Integer slot : stacks.keySet()) {
            clear(slot);
        }
    }

    @Override
    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        for (Integer slot : stacks.keySet()) {
            clear(slot);
        }
    }

    @Override
    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        for (Integer slot : stacks.keySet()) {
            clear(slot);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Clear

    @Override
    public void clear(int index) {
        setItem(index, null);
    }

    @Override
    public void clear() {
        for (int i = 0; i < getSize(); ++i) {
            clear(i);
        }
    }

}
