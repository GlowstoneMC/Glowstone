package net.glowstone.inventory;

import org.bukkit.Material;

public enum ClothType implements MaterialMatcher {
    LEATHER(Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE,
        Material.LEATHER_HELMET),
    CHAINMAIL(Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE,
        Material.CHAINMAIL_HELMET),
    IRON(Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE,
        Material.IRON_HELMET),
    GOLD(Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE,
        Material.GOLD_HELMET),
    DIAMOND(Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_HELMET);

    Material[] covering;

    ClothType(Material... covering) {
        this.covering = covering;
    }

    @Override
    public boolean matches(Material material) {
        for (Material c : covering) {
            if (c == material) {
                return true;
            }
        }
        return false;
    }
}
