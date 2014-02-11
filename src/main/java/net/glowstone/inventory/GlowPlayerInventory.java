package net.glowstone.inventory;

import net.glowstone.entity.GlowHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * An Inventory representing the items a player is holding.
 */
public class GlowPlayerInventory extends GlowInventory implements PlayerInventory {
    
    public static final int HELMET_SLOT = 36;
    public static final int CHESTPLATE_SLOT = 37;
    public static final int LEGGINGS_SLOT = 38;
    public static final int BOOTS_SLOT = 39;
    
    private final GlowCraftingInventory crafting = new GlowCraftingInventory(this);
    
    private int heldSlot = 0;

    public GlowPlayerInventory(GlowHumanEntity owner) {
        // all player inventories are ID 0
        // 36 = 4 rows of 9
        // + 4 = armor, completed inventory
        super(owner, InventoryType.PLAYER, 40);
    }
    
    /**
     * Get the crafting inventory.
     * @return The GlowCraftingInventory attached to this player
     */
    public GlowCraftingInventory getCraftingInventory() {
        return crafting;
    }

    /**
     * Set the slot number of the currently held item
     */
    public void setHeldItemSlot(int slot) {
        if (slot < 0) heldSlot = 0;
        else if (slot > 8) heldSlot = 8;
        else heldSlot = slot;
        setItemInHand(getItemInHand());
    }

    public ItemStack[] getArmorContents() {
        ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < 4; ++i) {
            armor[i] = getItem(HELMET_SLOT + i);
        }
        return armor;
    }

    public void setArmorContents(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Length of armor must be 4");
        }
        for (int i = 0; i < 4; ++i) {
            setItem(HELMET_SLOT + i, items[i]);
        }
    }

    public ItemStack getHelmet() {
        return getItem(HELMET_SLOT);
    }

    public ItemStack getChestplate() {
        return getItem(CHESTPLATE_SLOT);
    }

    public ItemStack getLeggings() {
        return getItem(LEGGINGS_SLOT);
    }

    public ItemStack getBoots() {
        return getItem(BOOTS_SLOT);
    }

    public void setHelmet(ItemStack helmet) {
        setItem(HELMET_SLOT, helmet);
    }

    public void setChestplate(ItemStack chestplate) {
        setItem(CHESTPLATE_SLOT, chestplate);
    }

    public void setLeggings(ItemStack leggings) {
        setItem(LEGGINGS_SLOT, leggings);
    }

    public void setBoots(ItemStack boots) {
        setItem(BOOTS_SLOT, boots);
    }

    public ItemStack getItemInHand() {
        return getItem(heldSlot);
    }

    public void setItemInHand(ItemStack stack) {
        setItem(heldSlot, stack);
    }

    public int getHeldItemSlot() {
        return heldSlot;
    }
    
    // Helper stuff
    
    private final static int slotConversion[] = {
        36, 37, 38, 39, 40, 41, 42, 43, 44, // quickbar
        9,  10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        5, 6, 7, 8 // armor
    };
    
    /**
     * Get the network index from a slot index.
     * @param itemSlot The index for use with getItem/setItem.
     * @return The index modified for transfer over the network, or -1 if there is no equivalent.
     */
    @Override
    public int getNetworkSlot(int itemSlot) {
        if (itemSlot > slotConversion.length) return -1;
        return slotConversion[itemSlot];
    }
    
    /**
     * Get the slot index from a network index.
     * @param networkSlot The index received over the network.
     * @return The index modified for use with getItem/setItem, or -1 if there is no equivalent.
     */
    @Override
    public int getItemSlot(int networkSlot) {
        for (int i = 0; i < slotConversion.length; ++i) {
            if (slotConversion[i] == networkSlot) return i;
        }
        return -1;
    }

    public int clear(int id, int data) {
        int cleared = 0;
        for (int i = 0; i < getSize(); ++i) {
            ItemStack stack = getItem(i);
            if ((stack.getTypeId() == id || id == -1) && (stack.getDurability() == data || data == -1)) {
                setItem(i, null);
                ++cleared;
            }
        }
        return cleared;
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) super.getHolder();
    }
}
