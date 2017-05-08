package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class ItemWearable extends ItemType {
    private int armorPoints;
    private boolean fastEquip;
    
    public ItemWearable(int points, ItemWearablePosition position, boolean equip) {
        armorPoints = points;
        wearablePosition = position;
        fastEquip = equip;
    }
    public int getArmorPoints() {
        return armorPoints;
    }
    @Override
    public void setWearablePosition(ItemWearablePosition position) {
        this.wearablePosition = position;
    }
    public void setArmorPoints(int points) {
        this.armorPoints = points;
    }
    public boolean getFastEquip() {
        return fastEquip;
    }
    public void setFastEquip(boolean equip) {
        this.fastEquip = equip;
    }
    
    @Override
    public boolean canOnlyUseSelf() {
        return fastEquip;
    }
    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        if (fastEquip) {
            equip(player, holding);
        }
    }
    
    public ItemStack equip(LivingEntity player, ItemStack armor) {
        if (wearablePosition == ItemWearablePosition.FEET) {
            if (player.getEquipment().getBoots().getType() == Material.AIR) {
                player.getEquipment().setBoots(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == ItemWearablePosition.LEGS) {
            if (player.getEquipment().getLeggings().getType() == Material.AIR) {
                player.getEquipment().setLeggings(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == ItemWearablePosition.CHEST) {
            if (player.getEquipment().getChestplate().getType() == Material.AIR) {
                player.getEquipment().setChestplate(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == ItemWearablePosition.HEAD) {
                if (player.getEquipment().getHelmet().getType() == Material.AIR) {
                player.getEquipment().setHelmet(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        return armor;
    }
}
