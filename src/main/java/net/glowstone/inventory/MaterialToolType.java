package net.glowstone.inventory;

import org.bukkit.Material;

public enum MaterialToolType implements MaterialMatcher {
    WOOD(Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_HOE),
    STONE(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_HOE),
    IRON(Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_HOE),
    GOLD(Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_HOE),
    DIAMOND(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE,
        Material.DIAMOND_HOE);

    Material[] covering;

    MaterialToolType(Material... covering) {
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
