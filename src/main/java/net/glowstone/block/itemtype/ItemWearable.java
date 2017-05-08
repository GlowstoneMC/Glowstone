package net.glowstone.block.itemtype;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack; 

public class ItemWearable extends ItemType {
    private int armorPoints;
    
    public ItemWearable(int points, ItemWearablePosition position) {
        armorPoints = points;
        wearablePosition = position;
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
