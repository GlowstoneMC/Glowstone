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
        return getSlot(index).getItem();
    }

    @Override
    public void setItem(int index, ItemStack item) {
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

        Iterator<GlowInventorySlot> iterator = slotIterator();
        while (toAdd > 0 && iterator.hasNext()) {
            GlowInventorySlot slot = iterator.next();
            // Look for existing stacks to add to
            ItemStack slotItem = slot.getItem();
            if (slotItem != null && compareItems(item, slotItem, ignoreMeta)) {
                int space = maxStackSize - slotItem.getAmount();
                if (space < 0) continue;
                if (space > toAdd) space = toAdd;

                slotItem.setAmount(slotItem.getAmount() + space);

                toAdd -= space;
            }
        }

        if (toAdd > 0) {
            // Look for empty slots to add to
            iterator = slotIterator();
            while (toAdd > 0 && iterator.hasNext()) {
                GlowInventorySlot slot = iterator.next();
                ItemStack slotItem = slot.getItem();
                if (slotItem == null) {
                    int num = toAdd > maxStackSize ? maxStackSize : toAdd;

                    slotItem = item.clone();
                    slotItem.setAmount(num);
                    slot.setItem(slotItem);

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

        Iterator<GlowInventorySlot> iterator = slotIterator();
        while (toRemove > 0 && iterator.hasNext()) {
            GlowInventorySlot slot = iterator.next();
            ItemStack slotItem = slot.getItem();
            // Look for stacks to remove from.
            if (slotItem != null && compareItems(item, slotItem, ignoreMeta)) {
                if (slotItem.getAmount() > toRemove) {
                    slotItem.setAmount(slotItem.getAmount() - toRemove);
                } else {
                    toRemove -= slotItem.getAmount();
                    slot.setItem(null);
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

        int i = 0;
        for (ItemStack itemStack : this) {
            contents[i] = itemStack;
            i++;
        }

        return contents;
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (items.length != getSize()) {
            throw new IllegalArgumentException("Length of items must be " + getSize());
        }

        Iterator<GlowInventorySlot> iterator = slotIterator();
        for (int i = 0; i < getSize(); i++) {
            iterator.next().setItem(items[i]);
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

        int i = 0;
        for (ItemStack slotItem : this) {
            if (slotItem != null && slotItem.getTypeId() == materialId) {
                result.put(i, slotItem);
            }

            i++;
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

        int i = 0;
        for (ItemStack slotItem : this) {
            if (slotItem != null && slotItem.equals(item)) {
                result.put(i, slotItem);
            }

            i++;
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find first

    @Override
    public int first(int materialId) {
        int i = 0;
        for (ItemStack slotItem : this) {
            if (slotItem == null) {
                if (materialId == 0) {
                    return i;
                }
            } else if (slotItem.getTypeId() == materialId) {
                return i;
            }

            i++;
        }

        return -1;
    }

    @Override
    public int first(Material material) {
        return first(material != null ? material.getId() : 0);
    }

    @Override
    public int first(ItemStack item) {
        int i = 0;
        for (ItemStack slotItem : this) {
            if (slotItem != null && slotItem.equals(item)) {
                return i;
            }

            i++;
        }

        return -1;
    }

    @Override
    public int firstEmpty() {
        return first((Material) null);
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
        Iterator<GlowInventorySlot> iterator = slotIterator();
        while (iterator.hasNext()) {
            iterator.next().setItem(null);
        }
    }

}
