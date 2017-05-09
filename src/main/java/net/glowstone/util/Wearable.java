package net.glowstone.util;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.Material;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Set;

public final class Wearable {
    
    private static Map<Material, WearableReturn> map;
    private static boolean intalized = false;
    
    private class WearableReturn {
        private EnchantmentTarget wearablePosition;
        private int armorPoints;
        
        public WearableReturn(EnchantmentTarget position, int points) {
            this.wearablePosition = position;
            this.armorPoints = points;
        }
        public EnchantmentTarget wearPosition() {
            return wearablePosition;
        }
        public int armorPoints() {
            return armorPoints;
        }
    }
    
    protected Map<Material, WearableReturn> createMap() {
        Map<Material, WearableReturn> map2 = new HashMap<>();
        map2.put(Material.LEATHER_BOOTS, new WearableReturn(EnchantmentTarget.ARMOR_FEET, 1));
        map2.put(Material.LEATHER_LEGGINGS, new WearableReturn(EnchantmentTarget.ARMOR_LEGS, 2));
        map2.put(Material.LEATHER_CHESTPLATE, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 3));
        map2.put(Material.LEATHER_HELMET, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 1));
        map2.put(Material.GOLD_BOOTS, new WearableReturn(EnchantmentTarget.ARMOR_FEET, 1));
        map2.put(Material.GOLD_LEGGINGS, new WearableReturn(EnchantmentTarget.ARMOR_LEGS, 3));
        map2.put(Material.GOLD_CHESTPLATE, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 5));
        map2.put(Material.GOLD_HELMET, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 2));
        map2.put(Material.IRON_BOOTS, new WearableReturn(EnchantmentTarget.ARMOR_FEET, 2));
        map2.put(Material.IRON_LEGGINGS, new WearableReturn(EnchantmentTarget.ARMOR_LEGS, 5));
        map2.put(Material.IRON_CHESTPLATE, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 6));
        map2.put(Material.IRON_HELMET, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 2));
        map2.put(Material.CHAINMAIL_BOOTS, new WearableReturn(EnchantmentTarget.ARMOR_FEET, 1));
        map2.put(Material.CHAINMAIL_LEGGINGS, new WearableReturn(EnchantmentTarget.ARMOR_LEGS, 4));
        map2.put(Material.CHAINMAIL_CHESTPLATE, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 5));
        map2.put(Material.CHAINMAIL_HELMET, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 2));
        map2.put(Material.DIAMOND_BOOTS, new WearableReturn(EnchantmentTarget.ARMOR_FEET, 3));
        map2.put(Material.DIAMOND_LEGGINGS, new WearableReturn(EnchantmentTarget.ARMOR_LEGS, 6));
        map2.put(Material.DIAMOND_CHESTPLATE, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 8));
        map2.put(Material.DIAMOND_HELMET, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 3));
        map2.put(Material.SKULL_ITEM, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 0));
        map2.put(Material.ELYTRA, new WearableReturn(EnchantmentTarget.ARMOR_TORSO, 0));
        map2.put(Material.PUMPKIN, new WearableReturn(EnchantmentTarget.ARMOR_HEAD, 0));
        return map;
    }
    
    public static void intalize() {
        if (!intalized) {
            map = (new Wearable()).createMap();
            intalized = true;
        }
    }
    
    public static WearableReturn find(Material material) {
        if (map.containsKey(material)) {
            return map.get(material);
        } else {
            return null;
        }
    }
    
    public static Set<Material> allWearables() {
        return map.keySet();
    }
    
    public static ItemStack equip(LivingEntity player, ItemStack armor) {
        return (new Wearable()).equipInstance(player, armor);
    }
    
    protected ItemStack equipInstance(LivingEntity player, ItemStack armor) {
        intalize();
        EnchantmentTarget wearablePosition = find(armor.getType()).wearPosition();
        if (wearablePosition == EnchantmentTarget.ARMOR_FEET) {
            if (player.getEquipment().getBoots().getType() == Material.AIR) {
                player.getEquipment().setBoots(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == EnchantmentTarget.ARMOR_LEGS) {
            if (player.getEquipment().getLeggings().getType() == Material.AIR) {
                player.getEquipment().setLeggings(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == EnchantmentTarget.ARMOR_TORSO) {
            if (player.getEquipment().getChestplate().getType() == Material.AIR) {
                player.getEquipment().setChestplate(armor.clone());
                armor.setAmount(armor.getAmount() - 1);
                if (armor.getAmount() == 0) armor.setType(Material.AIR);
                return armor;
            }
        }
        if (wearablePosition == EnchantmentTarget.ARMOR_HEAD) {
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
