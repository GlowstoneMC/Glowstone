package net.glowstone.inventory;

import org.bukkit.Material;

/**
 * A {@link MaterialMatcher} implementation for basic tool types.
 */
public enum ToolType implements MaterialMatcher {
    // Pickaxes
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, null),
    IRON_PICKAXE(Material.IRON_PICKAXE, DIAMOND_PICKAXE),
    STONE_PICKAXE(Material.STONE_PICKAXE, IRON_PICKAXE),
    GOLD_PICKAXE(Material.GOLD_PICKAXE, STONE_PICKAXE),
    PICKAXE(Material.WOOD_PICKAXE, GOLD_PICKAXE),

    // Spades
    DIAMOND_SPADE(Material.DIAMOND_SPADE, null),
    IRON_SPADE(Material.IRON_SPADE, DIAMOND_SPADE),
    STONE_SPADE(Material.STONE_SPADE, IRON_PICKAXE),
    GOLD_SPADE(Material.GOLD_SPADE, STONE_SPADE),
    SPADE(Material.WOOD_SPADE, GOLD_SPADE),

    // Swords
    DIAMOND_SWORD(Material.DIAMOND_SWORD, null),
    IRON_SWORD(Material.IRON_SWORD, DIAMOND_SWORD),
    STONE_SWORD(Material.STONE_SWORD, IRON_SWORD),
    GOLD_SWORD(Material.GOLD_SWORD, STONE_SWORD),
    SWORD(Material.WOOD_SWORD, GOLD_SWORD);

    private final Material bukkitMaterial;
    private final ToolType better;

    private ToolType(Material bukkitMaterial, ToolType better) {
        this.bukkitMaterial = bukkitMaterial;
        this.better = better;
    }

    /**
     * Checks the given {@link org.bukkit.Material} is equal or better than this ToolType.
     * @param material The material to check
     * @return true if the material is equal or better, false otherwise
     */
    @Override
    public boolean matches(Material material) {
        return bukkitMaterial == material || (better != null && better.matches(material));
    }
}
