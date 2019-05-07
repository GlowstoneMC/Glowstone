package net.glowstone.inventory;

import java.util.EnumMap;
import java.util.Map;
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
    GOLD_PICKAXE(Material.GOLDEN_PICKAXE, STONE_PICKAXE, ToolMaterial.GOLD),
    PICKAXE(Material.WOODEN_PICKAXE, GOLD_PICKAXE, ToolMaterial.WOOD),

    //Axes
    DIAMOND_AXE(Material.DIAMOND_AXE, null, ToolMaterial.DIAMOND),
    IRON_AXE(Material.IRON_AXE, DIAMOND_AXE, ToolMaterial.IRON),
    STONE_AXE(Material.STONE_AXE, IRON_AXE, ToolMaterial.STONE),
    GOLD_AXE(Material.GOLDEN_AXE, STONE_AXE, ToolMaterial.GOLD),
    AXE(Material.WOODEN_AXE, GOLD_AXE, ToolMaterial.WOOD),

    // SHOVELs
    DIAMOND_SHOVEL(Material.DIAMOND_SHOVEL, null, ToolMaterial.DIAMOND),
    IRON_SHOVEL(Material.IRON_SHOVEL, DIAMOND_SHOVEL, ToolMaterial.IRON),
    STONE_SHOVEL(Material.STONE_SHOVEL, IRON_SHOVEL, ToolMaterial.STONE),
    GOLD_SHOVEL(Material.GOLDEN_SHOVEL, STONE_SHOVEL, ToolMaterial.GOLD),
    SHOVEL(Material.WOODEN_SHOVEL, GOLD_SHOVEL, ToolMaterial.WOOD),

    // Swords all have the same breaking speed
    DIAMOND_SWORD(Material.DIAMOND_SWORD, null, ToolMaterial.SWORD),
    IRON_SWORD(Material.IRON_SWORD, DIAMOND_SWORD, ToolMaterial.SWORD),
    STONE_SWORD(Material.STONE_SWORD, IRON_SWORD, ToolMaterial.SWORD),
    GOLD_SWORD(Material.GOLDEN_SWORD, STONE_SWORD, ToolMaterial.SWORD),
    SWORD(Material.WOODEN_SWORD, GOLD_SWORD, ToolMaterial.SWORD),

    // Shears
    SHEARS(Material.SHEARS, null, ToolMaterial.SHEARS);

    private final Material bukkitMaterial;
    private final ToolType better;

    ToolType(Material bukkitMaterial, ToolType better, ToolMaterial toolMaterial) {
        this.bukkitMaterial = bukkitMaterial;
        this.better = better;
        ToolMaterial.MINING_MULTIPLIERS.put(bukkitMaterial, toolMaterial.getMultiplier());
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
    public static double getMiningMultiplier(Material tool) {
        return ToolMaterial.MINING_MULTIPLIERS.getOrDefault(tool, 1.0);
    }

    @RequiredArgsConstructor
    private enum ToolMaterial {
        WOOD(2),
        STONE(4),
        IRON(6),
        DIAMOND(8),
        GOLD(12),
        SHEARS(1.5),
        SWORD(1.5);

        /**
         * Can't move to outer class, because Java is too stupid to initialize other static members
         * before instantiating an enum.
         */
        static final Map<Material, Double> MINING_MULTIPLIERS
            = new EnumMap<>(Material.class);
        @Getter
        private final double multiplier;
    }
}
