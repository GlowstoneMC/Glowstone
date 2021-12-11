package net.glowstone.util;

import com.google.common.collect.ImmutableBiMap;
import jline.internal.Nullable;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public class WoolUtil {
    private WoolUtil() {
        // util class
    }

    private static final ImmutableBiMap<Material, DyeColor> DYE_BY_WOOL = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.WHITE_WOOL, DyeColor.WHITE)
            .put(Material.ORANGE_WOOL, DyeColor.ORANGE)
            .put(Material.MAGENTA_WOOL, DyeColor.MAGENTA)
            .put(Material.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE)
            .put(Material.YELLOW_WOOL, DyeColor.YELLOW)
            .put(Material.LIME_WOOL, DyeColor.LIME)
            .put(Material.PINK_WOOL, DyeColor.PINK)
            .put(Material.GRAY_WOOL, DyeColor.GRAY)
            .put(Material.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY)
            .put(Material.CYAN_WOOL, DyeColor.CYAN)
            .put(Material.PURPLE_WOOL, DyeColor.PURPLE)
            .put(Material.BLUE_WOOL, DyeColor.BLUE)
            .put(Material.BROWN_WOOL, DyeColor.BROWN)
            .put(Material.GREEN_WOOL, DyeColor.GREEN)
            .put(Material.RED_WOOL, DyeColor.RED)
            .put(Material.BLACK_WOOL, DyeColor.BLACK)
            .build();

    /**
     * If found, returns a {@link Material} linked to the dye color.
     * Otherwise, returns {@code null}.
     */
    @Nullable
    public static Material getWoolMaterialByDye(DyeColor color) {
        return DYE_BY_WOOL.inverse().get(color);
    }

    /**
     * If found, returns a {@link DyeColor} linked to the wool color.
     * Otherwise, returns {@code null}.
     */
    @Nullable
    public static DyeColor getDyeByWoolMaterial(Material material) {
        return DYE_BY_WOOL.get(material);
    }
}
