package net.glowstone.inventory;

import org.bukkit.Material;

public enum MaterialToolType implements MaterialMatcher {
    WOOD(Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE),
    STONE(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SHOVEL, Material.STONE_HOE),
    IRON(Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_HOE),
    GOLD(Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE),
    DIAMOND(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL,
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
