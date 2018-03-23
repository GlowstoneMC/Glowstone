package net.glowstone.util;

import lombok.Getter;
import net.glowstone.ServerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ImmutableItemStack extends ItemStack {

    @Getter // TODO: Defensive copy
    private final ItemMeta itemMeta;

    public ImmutableItemStack(int type) {
        super(type);
        itemMeta = ServerProvider.getServer().getItemFactory().getItemMeta(getType()).clone();
    }

    public ImmutableItemStack(Material type) {
        super(type);
        itemMeta = null;
    }

    public ImmutableItemStack(int type, int amount) {
        super(type, amount);
        itemMeta = null;
    }

    public ImmutableItemStack(Material type, int amount) {
        super(type, amount);
        itemMeta = null;
    }

    public ImmutableItemStack(int type, int amount, short damage) {
        super(type, amount, damage);
        itemMeta = null;
    }

    public ImmutableItemStack(Material type, int amount, short damage) {
        super(type, amount, damage);
        itemMeta = null;
    }

    public ImmutableItemStack(int type, int amount, short damage, Byte data) {
        super(type, amount, damage, data);
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
}
