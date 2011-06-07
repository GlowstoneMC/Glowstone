package net.glowstone.inventory;

import org.bukkit.inventory.*;

/**
 * An Inventory representing the items a player is holding.
 * @author Tad
 */
public class GlowPlayerInventory extends GlowInventory implements PlayerInventory {
    
    private ItemStack[] armor = new ItemStack[4];
    private int heldSlot = 0;

    public GlowPlayerInventory() {
        // all player inventories are ID 0
        // 36 = 4 rows of 9 = complete player inventory
        super((byte) 0, 36);
    }

    /**
     * Return the name of the inventory
     *
     * @return The inventory name
     */
    @Override
    public String getName() {
        return "Player Inventory";
    }

    /**
     * Set the slot number of the currently held item
     *
     * @return Held item slot number
     */
    public void setHeldItemSlot(int slot) {
        if (slot < 0) heldSlot = 0;
        else if (slot > 8) heldSlot = 8;
        else heldSlot = slot;
    }

    public ItemStack[] getArmorContents() {
        return armor;
    }

    public void setArmorContents(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Length of armor must be 4");
        }
        armor = items;
    }

    public ItemStack getHelmet() {
        return armor[0];
    }

    public ItemStack getChestplate() {
        return armor[1];
    }

    public ItemStack getLeggings() {
        return armor[2];
    }

    public ItemStack getBoots() {
        return armor[3];
    }

    public void setHelmet(ItemStack helmet) {
        armor[0] = helmet;
    }

    public void setChestplate(ItemStack chestplate) {
        armor[1] = chestplate;
    }

    public void setLeggings(ItemStack leggings) {
        armor[2] = leggings;
    }

    public void setBoots(ItemStack boots) {
        armor[3] = boots;
    }

    public ItemStack getItemInHand() {
        return getItem(heldSlot);
    }

    public void setItemInHand(ItemStack stack) {
        super.setItem(heldSlot, stack);
    }

    public int getHeldItemSlot() {
        return heldSlot;
    }
    
    // Helper static functions
    
    private final static int slotConversion[] = {
        36, 37, 38, 39, 40, 41, 42, 43, 44, // quickbar
        9,  10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        5, 6, 7, 8, // armor
        1, 2, 3, 4, // crafting
        0 // crafting result
    };
    
    public static int inventorySlotToNetwork(int slot) {
        return slotConversion[slot];
    }
    
    public static int networkSlotToInventory(int slot) {
        for (int i = 0; i < slotConversion.length; ++i) {
            if (slotConversion[i] == slot) return i;
        }
        return -1;
    }

}
