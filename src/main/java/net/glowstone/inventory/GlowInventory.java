package net.glowstone.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowInventory implements Inventory {
    
    /**
     * The ID of the inventory.
     */
    private final byte id;

    /**
     * The list of InventoryViewers attached to this inventory.
     */
    protected final ArrayList<InventoryViewer> viewers = new ArrayList<InventoryViewer>();
    
    /**
     * This inventory's contents.
     */
    private final ItemStack[] slots;

    /**
     * Initialize the inventory 
     * @param size 
     */
    protected GlowInventory(byte id, int size) {
        this.id = id;
        slots = new ItemStack[size];
    }
    
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

    // Basic Stuff ///////////////
    
    /**
     * Gets the inventory ID.
     * @return The inventory id for wire purposes.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the size of the inventory
     *
     * @return The inventory size
     */
    public int getSize() {
        return slots.length;
    }

    /**
     * Return the name of the inventory
     *
     * @return The inventory name
     */
    public String getName() {
        return "Generic Inventory";
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

    // Get, Set, Add, Remove /////

    /**
     * Get the ItemStack found in the slot at the given index
     *
     * @param index The index of the Slot's ItemStack to return
     * @return The ItemStack in the slot
     */
    public ItemStack getItem(int index) {
        return slots[index];
    }

    /**
     * Stores the ItemStack at the given index.
     * Notifies all attached InventoryViewers of the change.
     *
     * @param index The index where to put the ItemStack
     * @param item The ItemStack to set
     */
    public void setItem(int index, ItemStack item) {
        slots[index] = item;
        sendUpdate(index);
    }

    /**
     * Stores the given ItemStacks in the inventory.
     *
     * This will try to fill existing stacks and empty slots as good as it can.
     * It will return a HashMap of what it couldn't fit.
     *
     * @param items The ItemStacks to add
     * @return
     */
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; ++i) {
            Material mat = items[i].getType();
            int toAdd = items[i].getAmount();
            short damage = items[i].getDurability();
            
            for (int j = 0; toAdd > 0 && j < getSize(); ++j) {
                // Look for existing stacks to add to
                if (slots[j] != null && slots[j].getType() == mat && slots[j].getDurability() == damage) {
                    int space = mat.getMaxStackSize() - slots[j].getAmount();
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
                        int num = toAdd > mat.getMaxStackSize() ? mat.getMaxStackSize() : toAdd;
                        slots[j] = new ItemStack(mat, num, damage);
                        toAdd -= num;
                        sendUpdate(j);
                    }
                }
            }
            
            if (toAdd > 0) {
                // Still couldn't stash them all.
                result.put(i, new ItemStack(mat, toAdd, damage));
            }
        }
        
        return result;
    }

    /**
     * Removes the given ItemStacks from the inventory.
     *
     * It will try to remove 'as much as possible' from the types and amounts you
     * give as arguments. It will return a HashMap of what it couldn't remove.
     *
     * @param items The ItemStacks to remove
     * @return
     */
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; ++i) {
            Material mat = items[i].getType();
            int toRemove = items[i].getAmount();
            short damage = items[i].getDurability();
            
            for (int j = 0; j < getSize(); ++j) {
                // Look for stacks to remove from.
                if (slots[j] != null && slots[j].getType() == mat && slots[j].getDurability() == damage) {
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
                result.put(i, new ItemStack(mat, toRemove, damage));
            }
        }
        
        return result;
    }

    /**
     * Get all ItemStacks from the inventory
     *
     * @return All the ItemStacks from all slots
     */
    public ItemStack[] getContents() {
        return slots;
    }

    /**
     * Set the inventory's contents
     *
     * @return All the ItemStacks from all slots
     */
    public void setContents(ItemStack[] items) {
        if (items.length != slots.length) {
            throw new IllegalArgumentException("Length of items must be " + slots.length);
        }
        for (int i = 0; i < items.length; ++i) {
            setItem(i, items[i]);
        }
    }

    // Contains family ///////////

    /**
     * Check if the inventory contains any ItemStacks with the given materialId
     *
     * @param materialId The materialId to check for
     * @return If any ItemStacks were found
     */
    public boolean contains(int materialId) {
        return first(materialId) >= 0;
    }

    /**
     * Check if the inventory contains any ItemStacks with the given material
     *
     * @param material The material to check for
     * @return If any ItemStacks were found
     */
    public boolean contains(Material material) {
        return first(material) >= 0;
    }

    /**
     * Check if the inventory contains any ItemStacks matching the given ItemStack
     * This will only match if both the type and the amount of the stack match
     *
     * @param item The ItemStack to match against
     * @return If any matching ItemStacks were found
     */
    public boolean contains(ItemStack item) {
        return first(item) >= 0;
    }

    /**
     * Check if the inventory contains any ItemStacks with the given materialId and at least the minimum amount specified
     *
     * @param materialId The materialId to check for
     * @param amount The minimum amount to look for
     * @return If any ItemStacks were found
     */
    public boolean contains(int materialId, int amount) {
        HashMap<Integer, ? extends ItemStack> found = all(materialId);
        int total = 0;
        for (ItemStack stack : found.values()) {
            total += stack.getAmount();
        }
        return total >= amount;
    }

    /**
     * Check if the inventory contains any ItemStacks with the given material and at least the minimum amount specified
     *
     * @param material The material to check for
     * @return If any ItemStacks were found
     */
    public boolean contains(Material material, int amount) {
        return contains(material.getId(), amount);
    }

    /**
     * Check if the inventory contains any ItemStacks matching the given ItemStack and at least the minimum amount specified
     * This will only match if both the type and the amount of the stack match
     *
     * @param item The ItemStack to match against
     * @return If any matching ItemStacks were found
     */
    public boolean contains(ItemStack item, int amount) {
        return contains(item.getTypeId(), amount);
    }

    // All Family ////////////////

    /**
     * Find all slots in the inventory containing any ItemStacks with the given materialId
     *
     * @param materialId The materialId to look for
     * @return The Slots found.
     */
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i].getTypeId() == materialId) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    /**
     * Find all slots in the inventory containing any ItemStacks with the given material
     *
     * @param materialId The material to look for
     * @return The Slots found.
     */
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return all(material.getId());
    }

    /**
     * Find all slots in the inventory containing any ItemStacks with the given ItemStack
     * This will only match slots if both the type and the amount of the stack match
     *
     * @param item The ItemStack to match against
     * @return The Slots found.
     */
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    // First Family //////////////

    /**
     * Find the first slot in the inventory containing an ItemStack with the given materialId
     *
     * @param materialId The materialId to look for
     * @return The Slot found.
     */
    public int first(int materialId) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].getTypeId() == materialId) return i;
        }
        return -1;
    }

    /**
     * Find the first slot in the inventory containing an ItemStack with the given material
     *
     * @param materialId The material to look for
     * @return The Slot found.
     */
    public int first(Material material) {
        return first(material.getId());
    }

    /**
     * Find the first slot in the inventory containing an ItemStack with the given stack
     * This will only match a slot if both the type and the amount of the stack match
     *
     * @param item The ItemStack to match against
     * @return The Slot found.
     */
    public int first(ItemStack item) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) return i;
        }
        return -1;
    }

    /**
     * Find the first empty Slot.
     *
     * @return The first empty Slot found.
     */
    public int firstEmpty() {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    // Remove Family /////////////

    /**
     * Remove all stacks in the inventory matching the given materialId.
     *
     * @param materialId The material to remove
     */
    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    /**
     * Remove all stacks in the inventory matching the given material.
     *
     * @param material The material to remove
     */
    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    /**
     * Remove all stacks in the inventory matching the given stack.
     * This will only match a slot if both the type and the amount of the stack match
     *
     * @param item The ItemStack to match against
     */
    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    // Clear Family //////////////

    /**
     * Clear out a particular slot in the index
     *
     * @param index The index to empty.
     */
    public void clear(int index) {
        setItem(index, null);
    }

    /**
     * Clear out the whole index
     */
    public void clear() {
        for (int i = 0; i < slots.length; ++i) {
            clear(i);
        }
    }
    
}
