package net.glowstone.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * A {@link MaterialMatcher} implementation for basic tool types.
 */
public enum ToolType implements MaterialMatcher {
    // Pickaxes
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, null, ToolMaterial.DIAMOND),
    IRON_PICKAXE(Material.IRON_PICKAXE, DIAMOND_PICKAXE, ToolMaterial.IRON),
    STONE_PICKAXE(Material.STONE_PICKAXE, IRON_PICKAXE, ToolMaterial.STONE),
    GOLD_PICKAXE(Material.GOLD_PICKAXE, STONE_PICKAXE, ToolMaterial.GOLD),
    PICKAXE(Material.WOOD_PICKAXE, GOLD_PICKAXE, ToolMaterial.WOOD),

    //Axes
    DIAMOND_AXE(Material.DIAMOND_AXE, null, ToolMaterial.DIAMOND),
    IRON_AXE(Material.IRON_AXE, DIAMOND_AXE, ToolMaterial.IRON),
    STONE_AXE(Material.STONE_AXE, IRON_AXE, ToolMaterial.STONE),
    GOLD_AXE(Material.GOLD_AXE, STONE_AXE, ToolMaterial.GOLD),
    AXE(Material.WOOD_AXE, GOLD_AXE, ToolMaterial.WOOD),

    // Spades
    DIAMOND_SPADE(Material.DIAMOND_SPADE, null, ToolMaterial.DIAMOND),
    IRON_SPADE(Material.IRON_SPADE, DIAMOND_SPADE, ToolMaterial.IRON),
    STONE_SPADE(Material.STONE_SPADE, IRON_SPADE, ToolMaterial.STONE),
    GOLD_SPADE(Material.GOLD_SPADE, STONE_SPADE, ToolMaterial.GOLD),
    SPADE(Material.WOOD_SPADE, GOLD_SPADE, ToolMaterial.WOOD),

    // Swords
    DIAMOND_SWORD(Material.DIAMOND_SWORD, null, ToolMaterial.DIAMOND),
    IRON_SWORD(Material.IRON_SWORD, DIAMOND_SWORD, ToolMaterial.IRON),
    STONE_SWORD(Material.STONE_SWORD, IRON_SWORD, ToolMaterial.STONE),
    GOLD_SWORD(Material.GOLD_SWORD, STONE_SWORD, ToolMaterial.GOLD),
    SWORD(Material.WOOD_SWORD, GOLD_SWORD, ToolMaterial.WOOD),

    // Shears
    SHEARS(Material.SHEARS, null, ToolMaterial.SHEARS);

    private final Material bukkitMaterial;
    private final ToolType better;
    private final ToolMaterial toolMaterial;

    ToolType(Material bukkitMaterial, ToolType better, ToolMaterial toolMaterial) {
        this.bukkitMaterial = bukkitMaterial;
        this.better = better;
        this.toolMaterial = toolMaterial;
    }

    /**
     * Checks the given {@link Material} is equal or better than this ToolType.
     *
     * @param material The material to check
     * @return true if the material is equal or better, false otherwise
     */
    @Override
    public boolean matches(Material material) {
        return bukkitMaterial == material || better != null && better.matches(material);
    }

    /**
     * Get the factor to multiply mining speed with if this tool is used to mine.
     *
     * @return the multiplier
     */
    public double getMiningMultiplier() {
        return this.toolMaterial.getMultiplier();
    }

    @RequiredArgsConstructor
    private enum ToolMaterial {
        WOOD(2),
        STONE(4),
        IRON(6),
        DIAMOND(8),
        GOLD(12),
        SHEARS(1.5);

        @Getter
        private final double multiplier;
    }
}
