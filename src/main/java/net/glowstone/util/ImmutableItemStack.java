package net.glowstone.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ImmutableItemStack extends ItemStack {

    private final ItemMeta itemMeta;

    public ImmutableItemStack(Material type) {
        super(type);
        itemMeta = null;
    }

    public ImmutableItemStack(Material type, int amount) {
        super(type, amount);
        itemMeta = null;
    }

    public ImmutableItemStack(Material type, int amount, short damage) {
        super(type, amount, damage);
        itemMeta = null;
    }

    public ImmutableItemStack(Material type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
        itemMeta = null;
    }

    public ImmutableItemStack(ItemStack stack) throws IllegalArgumentException {
        super(stack);
        itemMeta = stack.getItemMeta().clone();
    }

    @Deprecated
    @Override
    public void setType(Material type) {
    }

    @Deprecated
    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return false;
    }

    @Deprecated
    @Override
    public void setAmount(int amount) {
    }

    @Deprecated
    @Override
    public void setData(MaterialData data) {
    }

    @Deprecated
    @Override
    public void setDurability(short durability) {
    }

    @Override
    public ItemMeta getItemMeta() {
        return itemMeta.clone();
    }
}
