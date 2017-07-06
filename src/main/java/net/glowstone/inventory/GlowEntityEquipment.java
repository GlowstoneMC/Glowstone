package net.glowstone.inventory;

import net.glowstone.util.InventoryUtil;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GlowEntityEquipment implements EntityEquipment {

    private ItemStack[] slots = new ItemStack[6];
    private Entity holder;

    public GlowEntityEquipment(Entity holder) {
        this.holder = holder;
    }

    /**
     * Returns the ItemStack found in the slot at the given EquipmentSlot
     * @param slot The EquipmentSlot of the Slot's ItemStack to return
     * @return The ItemStack in the slot, or null, if no item is in this slot.
     */
    public ItemStack getItem(EquipmentSlot slot) {
        return slots[slot.ordinal()];
    }

    /**
     * Stores the ItemStack at the given index of the inventory.
     * @param slot The EquipmentSlot where to put the ItemStack
     * @param item The ItemStack to set
     */
    public void setItem(EquipmentSlot slot, ItemStack item) {
        slots[slot.ordinal()] = item;
    }

    @Override
    public ItemStack getItemInMainHand() {
        return getItem(EquipmentSlot.HAND);
    }

    @Override
    public void setItemInMainHand(ItemStack itemStack) {
        setItem(EquipmentSlot.HAND, itemStack);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return getItem(EquipmentSlot.OFF_HAND);
    }

    @Override
    public void setItemInOffHand(ItemStack itemStack) {
        setItem(EquipmentSlot.OFF_HAND, itemStack);
    }

    @Override
    public ItemStack getItemInHand() {
        return getItemInMainHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        setItemInMainHand(itemStack);
    }

    @Override
    public ItemStack getHelmet() {
        return getItem(EquipmentSlot.HEAD);
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        setItem(EquipmentSlot.HEAD, itemStack);
    }

    @Override
    public ItemStack getChestplate() {
        return getItem(EquipmentSlot.CHEST);
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        setItem(EquipmentSlot.CHEST, itemStack);
    }

    @Override
    public ItemStack getLeggings() {
        return getItem(EquipmentSlot.LEGS);
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        setItem(EquipmentSlot.LEGS, itemStack);
    }

    @Override
    public ItemStack getBoots() {
        return getItem(EquipmentSlot.FEET);
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        setItem(EquipmentSlot.FEET, itemStack);
    }

    @Override
    public ItemStack[] getArmorContents() {
        ItemStack[] armor = new ItemStack[4];
        int feet = EquipmentSlot.FEET.ordinal();
        for (int i = feet; i < slots.length; i++) {
            armor[i - feet] = getItem(EquipmentSlot.values()[i]);
        }
        return armor;
    }

    @Override
    public void setArmorContents(ItemStack[] itemStacks) {
        if (itemStacks.length != slots.length) {
            throw new IllegalArgumentException("Length of armor must be " + slots.length);
        }
        for (int i = EquipmentSlot.FEET.ordinal(); i < slots.length; i++) {
            setItem(EquipmentSlot.values()[i], itemStacks[i]);
        }
    }

    @Override
    public void clear() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            setItem(slot, InventoryUtil.createEmptyStack());
        }
    }

    @Override
    public float getItemInHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getItemInMainHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInMainHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getItemInOffHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInOffHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getHelmetDropChance() {
        return 1;
    }

    @Override
    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getChestplateDropChance() {
        return 1;
    }

    @Override
    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getLeggingsDropChance() {
        return 1;
    }

    @Override
    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getBootsDropChance() {
        return 1;
    }

    @Override
    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entity getHolder() {
        return this.holder;
    }
}
