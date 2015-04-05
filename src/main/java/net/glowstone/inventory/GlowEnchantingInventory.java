package net.glowstone.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GlowEnchantingInventory extends GlowInventory implements EnchantingInventory {

    private static final int ITEM_SLOT = 0;
    private static final int LAPIS_SLOT = 1;

    public GlowEnchantingInventory(InventoryHolder holder) {
        super(holder, InventoryType.ENCHANTING);

        getSlot(ITEM_SLOT).setType(InventoryType.SlotType.CRAFTING);
        getSlot(LAPIS_SLOT).setType(InventoryType.SlotType.CRAFTING);
    }

    @Override
    public int getRawSlots() {
        return 0;
    }

    @Override
    public void setItem(ItemStack item) {
        setItem(ITEM_SLOT, item);
    }

    @Override
    public ItemStack getItem() {
        return getItem(ITEM_SLOT);
    }

    @Override
    public void setSecondary(ItemStack itemStack) {
        setItem(LAPIS_SLOT, itemStack);
    }

    @Override
    public ItemStack getSecondary() {
        return getItem(LAPIS_SLOT);
    }
}
