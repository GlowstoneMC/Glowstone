package net.glowstone.inventory;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowInventory implements Inventory {

    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ItemStack getItem(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItem(int index, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ItemStack[] getContents() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setContents(ItemStack[] items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(int materialId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(Material material) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(int materialId, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(Material material, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(ItemStack item, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int first(int materialId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int first(Material material) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int first(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int firstEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(int materialId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(Material material) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
