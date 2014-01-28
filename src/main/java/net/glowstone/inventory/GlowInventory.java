package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowInventory implements Inventory {
    
    /**
     * The ID of the inventory.
     * Todo: improve this - only implemented inventory is player which is always 0.
     */
    private final byte id = 0;

    /**
     * The list of InventoryViewers attached to this inventory.
     */
    protected final ArrayList<InventoryViewer> viewers = new ArrayList<InventoryViewer>();

    /**
     * The owner of this inventory.
     */
    private final InventoryHolder owner;

    /**
     * The type of this inventory.
     */
    private final InventoryType type;
    
    /**
     * This inventory's contents.
     */
    private final GlowItemStack[] slots;

    /**
     * The inventory's name.
     */
    private final String title;

    /**
     * The inventory's maximum stack size.
     */
    private int maxStackSize = 64;

    protected GlowInventory(InventoryHolder owner, InventoryType type) {
        this(owner, type, type.getDefaultSize(), type.getDefaultTitle());
    }

    protected GlowInventory(InventoryHolder owner, InventoryType type, int size) {
        this(owner, type, size, type.getDefaultTitle());
    }

    protected GlowInventory(InventoryHolder owner, InventoryType type, int size, String title) {
        this.owner = owner;
        this.type = type;
        this.slots = new GlowItemStack[size];
        this.title = title;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals
    
    /**
     * Add a viewer to the inventory.
     * @param viewer The InventoryViewer to add.
     */
    public void addViewer(InventoryViewer viewer) {
        if (!viewers.contains(viewer)) {
            viewers.add(viewer);
        }
    }
    
    /**
     * Remove a viewer from the inventory.
     * @param viewer The InventoryViewer to remove.
     */
    public void removeViewer(InventoryViewer viewer) {
        if (viewers.contains(viewer)) {
            viewers.remove(viewer);
        }
    }
    
    /**
     * Get the network index from a slot index.
     * @param itemSlot The index for use with getItem/setItem.
     * @return The index modified for transfer over the network, or -1 if there is no equivalent.
     */
    public int getNetworkSlot(int itemSlot) {
        return itemSlot;
    }
    
    /**
     * Get the slot index from a network index.
     * @param networkSlot The index received over the network.
     * @return The index modified for use with getItem/setItem, or -1 if there is no equivalent.
     */
    public int getItemSlot(int networkSlot) {
        return networkSlot;
    }

    /**
     * Gets the inventory ID.
     * @return The inventory id for wire purposes.
     */
    public int getId() {
        return id;
    }

    /**
     * Updates all attached inventory viewers about a change to index.
     * @param index The index to update.
     */
    protected void sendUpdate(int index) {
        for (InventoryViewer viewer : viewers) {
            viewer.onSlotSet(this, index, slots[index]);
        }
    }

    /**
     * Get a GlowItemStack from an ItemStack.
     * @param stack The ItemStack.
     * @return The GlowItemStack.
     */
    public static GlowItemStack getGlowItemStack(ItemStack stack) {
        if (stack == null) {
            return null;
        } else if (stack instanceof GlowItemStack) {
            return (GlowItemStack) stack;
        } else {
            return new GlowItemStack(stack);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    public final int getSize() {
        return slots.length;
    }

    public final InventoryType getType() {
        return type;
    }

    public InventoryHolder getHolder() {
        return owner;
    }

    public final String getName() {
        return title;
    }

    public final String getTitle() {
        return title;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
    }

    public List<HumanEntity> getViewers() {
        List<HumanEntity> result = new LinkedList<HumanEntity>();
        for (InventoryViewer viewer : viewers) {
            if (viewer instanceof HumanEntity) {
                result.add((HumanEntity) viewer);
            }
        }
        return result;
    }

    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            // negative indices go from back
            index += getSize() + 1;
        }
        return new InventoryIterator(this, index);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get, Set, Add, Remove

    public GlowItemStack getItem(int index) {
        return slots[index];
    }

    public void setItem(int index, GlowItemStack item) {
        slots[index] = item;
        sendUpdate(index);
    }
    
    public final void setItem(int index, ItemStack item) {
        setItem(index, getGlowItemStack(item));
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; ++i) {
            int maxStackSize = items[i].getType() == null ? 64 : items[i].getType().getMaxStackSize();
            int mat = items[i].getTypeId();
            int toAdd = items[i].getAmount();
            short damage = items[i].getDurability();
            
            for (int j = 0; toAdd > 0 && j < getSize(); ++j) {
                // Look for existing stacks to add to
                if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
                    int space = maxStackSize - slots[j].getAmount();
                    if (space < 0) continue;
                    if (space > toAdd) space = toAdd;
                    
                    slots[j].setAmount(slots[j].getAmount() + space);
                    toAdd -= space;
                    sendUpdate(j);
                }
            }
            
            if (toAdd > 0) {
                // Look for empty slots to add to
                for (int j = 0; toAdd > 0 && j < getSize(); ++j) {
                    if (slots[j] == null) {
                        int num = toAdd > maxStackSize ? maxStackSize : toAdd;
                        slots[j] = new GlowItemStack(mat, num, damage);
                        toAdd -= num;
                        sendUpdate(j);
                    }
                }
            }
            
            if (toAdd > 0) {
                // Still couldn't stash them all.
                result.put(i, new GlowItemStack(mat, toAdd, damage));
            }
        }
        
        return result;
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; ++i) {
            int mat = items[i].getTypeId();
            int toRemove = items[i].getAmount();
            short damage = items[i].getDurability();
            
            for (int j = 0; j < getSize(); ++j) {
                // Look for stacks to remove from.
                if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
                    if (slots[j].getAmount() > toRemove) {
                        slots[j].setAmount(slots[j].getAmount() - toRemove);
                    } else {
                        toRemove -= slots[j].getAmount();
                        slots[j] = null;
                    }
                    sendUpdate(j);
                }
            }
            
            if (toRemove > 0) {
                // Couldn't remove them all.
                result.put(i, new GlowItemStack(mat, toRemove, damage));
            }
        }
        
        return result;
    }

    public GlowItemStack[] getContents() {
        return slots;
    }

    public void setContents(ItemStack[] items) {
        if (items.length != slots.length) {
            throw new IllegalArgumentException("Length of items must be " + slots.length);
        }
        for (int i = 0; i < items.length; ++i) {
            setItem(i, items[i]);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Contains

    public boolean contains(int materialId) {
        return first(materialId) >= 0;
    }

    public boolean contains(Material material) {
        return first(material) >= 0;
    }

    public boolean contains(ItemStack item) {
        return first(item) >= 0;
    }

    public boolean contains(int materialId, int amount) {
        HashMap<Integer, ? extends ItemStack> found = all(materialId);
        int total = 0;
        for (ItemStack stack : found.values()) {
            total += stack.getAmount();
        }
        return total >= amount;
    }

    public boolean contains(Material material, int amount) {
        return contains(material.getId(), amount);
    }

    public boolean contains(ItemStack item, int amount) {
        return contains(item.getTypeId(), amount);
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        return false;  // todo
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find all

    public HashMap<Integer, GlowItemStack> all(int materialId) {
        HashMap<Integer, GlowItemStack> result = new HashMap<Integer, GlowItemStack>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i].getTypeId() == materialId) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    public HashMap<Integer, GlowItemStack> all(Material material) {
        return all(material.getId());
    }

    public HashMap<Integer, GlowItemStack> all(ItemStack item) {
        HashMap<Integer, GlowItemStack> result = new HashMap<Integer, GlowItemStack>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find first

    public int first(int materialId) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].getTypeId() == materialId) return i;
        }
        return -1;
    }

    public int first(Material material) {
        return first(material.getId());
    }

    public int first(ItemStack item) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) return i;
        }
        return -1;
    }

    public int firstEmpty() {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Remove

    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Clear

    public void clear(int index) {
        setItem(index, null);
    }

    public void clear() {
        for (int i = 0; i < slots.length; ++i) {
            clear(i);
        }
    }
    
}
