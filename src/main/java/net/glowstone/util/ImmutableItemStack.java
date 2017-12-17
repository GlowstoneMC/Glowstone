package net.glowstone.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ImmutableItemStack extends ItemStack {

    private ItemMeta originalMeta = null;

    public ImmutableItemStack(int type) {
        super(type);
        originalMeta = Bukkit.getItemFactory().getItemMeta(getType()).clone();
    }

    public ImmutableItemStack(Material type) {
        super(type);
    }

    public ImmutableItemStack(int type, int amount) {
        super(type, amount);
    }

    public ImmutableItemStack(Material type, int amount) {
        super(type, amount);
    }

    public ImmutableItemStack(int type, int amount, short damage) {
        super(type, amount, damage);
    }

    public ImmutableItemStack(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public ImmutableItemStack(int type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public ImmutableItemStack(Material type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
    }

    public ImmutableItemStack(ItemStack stack) throws IllegalArgumentException {
        super(stack);
        originalMeta = stack.getItemMeta().clone();
    }

    @Deprecated
    @Override
    public void setType(Material type) {
    }

    @Deprecated
    @Override
    public void setTypeId(int type) {
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
        return originalMeta;
    }
}
