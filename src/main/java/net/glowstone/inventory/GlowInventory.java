package net.glowstone.inventory;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowInventory implements Inventory {

    protected ItemStack[] slots;

    public GlowInventory(int size) {
        slots = new ItemStack[size];
    }

    // Basic Stuff ///////////////

    public int getSize() {
        return slots.length;
    }

    public String getName() {
        return "Generic Inventory";
    }

    // Get, Set, Add, Remove /////

    public ItemStack getItem(int index) {
        return slots[index];
    }

    public void setItem(int index, ItemStack item) {
        slots[index] = item;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        for (ItemStack stack : items) {
            int open = firstEmpty();
            if (open < 0) break;
            slots[open] = stack;
            result.put(open, stack);
        }
        return result;
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        for (ItemStack stack : items) {
            HashMap<Integer, ? extends ItemStack> stacks = all(stack);
            for (Integer slot : stacks.keySet()) {
                slots[slot] = null;
                result.put(slot, stacks.get(slot));
            }
        }
        return result;
    }

    public ItemStack[] getContents() {
        return slots;
    }

    public void setContents(ItemStack[] items) {
        slots = items;
    }

    // Contains family ///////////

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

    // All Family ////////////////

    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i].getTypeId() == materialId) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return all(material.getId());
    }

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

    // Remove Family /////////////

    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        for (Integer slot : stacks.keySet()) {
            slots[slot] = null;
        }
    }

    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        for (Integer slot : stacks.keySet()) {
            slots[slot] = null;
        }
    }

    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        for (Integer slot : stacks.keySet()) {
            slots[slot] = null;
        }
    }

    // Clear Family //////////////

    public void clear(int index) {
        slots[index] = null;
    }

    public void clear() {
        for (int i = 0; i < slots.length; ++i) {
            slots[i] = null;
        }
    }
    
}
