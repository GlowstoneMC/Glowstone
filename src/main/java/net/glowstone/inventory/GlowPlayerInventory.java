package net.glowstone.inventory;

import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * An Inventory representing the items a player is holding.
 */
public class GlowPlayerInventory extends GlowInventory implements PlayerInventory, EntityEquipment {

    private static final int SIZE = 36;

    private static final int BOOTS_SLOT = 36;
    private static final int LEGGINGS_SLOT = 37;
    private static final int CHESTPLATE_SLOT = 38;
    private static final int HELMET_SLOT = 39;

    /**
     * The armor contents.
     */
    private final ItemStack[] armor = new ItemStack[4];

    /**
     * The crafting inventory.
     */
    private final GlowCraftingInventory crafting = new GlowCraftingInventory(this);

    /**
     * The current held item slot.
     */
    private int heldSlot = 0;

    public GlowPlayerInventory(GlowHumanEntity owner) {
        // all player inventories are ID 0
        // 36 = 4 rows of 9
        // + 4 = armor, completed inventory
        super(owner, InventoryType.PLAYER, SIZE);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Get the crafting inventory.
     * @return The GlowCraftingInventory attached to this player
     */
    public GlowCraftingInventory getCraftingInventory() {
        return crafting;
    }

    public void setRawHeldItemSlot(int slot) {
        if (slot < 0 || slot > 8)
            throw new IllegalArgumentException(slot + " not in range 0..8");
        heldSlot = slot;
        setItemInHand(getItemInHand());  // send to player again just in case
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overrides

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) super.getHolder();
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index >= SIZE) {
            armor[index - SIZE] = item;
        } else {
            super.setItem(index, item);
        }
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= SIZE) {
            return armor[index - SIZE];
        } else {
            return super.getItem(index);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Interface implementation

    public ItemStack[] getArmorContents() {
        return armor;
    }

    public void setArmorContents(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Length of armor must be 4");
        }
        for (int i = 0; i < 4; ++i) {
            setItem(SIZE + i, items[i]);
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

    public void setHeldItemSlot(int slot) {
        setRawHeldItemSlot(slot);

        if (getHolder() instanceof GlowPlayer) {
            ((GlowPlayer) getHolder()).getSession().send(new HeldItemMessage(slot));
        }
    }

    public int clear(int id, int data) {
        int cleared = 0;
        for (int i = 0; i < getSize(); ++i) {
            ItemStack stack = getItem(i);
            if (stack != null && (stack.getTypeId() == id || id == -1) && (stack.getDurability() == data || data == -1)) {
                setItem(i, null);
                ++cleared;
            }
        }
        return cleared;
    }

    ////////////////////////////////////////////////////////////////////////////
    // EntityEquipment implementation

    public float getItemInHandDropChance() {
        return 1;
    }

    public void setItemInHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getHelmetDropChance() {
        return 1;
    }

    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getChestplateDropChance() {
        return 1;
    }

    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getLeggingsDropChance() {
        return 1;
    }

    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getBootsDropChance() {
        return 1;
    }

    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }
}
