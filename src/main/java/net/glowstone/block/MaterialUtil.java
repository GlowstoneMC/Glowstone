package net.glowstone.block;

import com.google.common.collect.ImmutableSet;
import java.util.EnumSet;
import org.bukkit.Material;

public class MaterialUtil {
    public static final ImmutableSet<Material> WOOLS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_WOOL, Material.YELLOW_WOOL, Material.PINK_WOOL, Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL, Material.LIME_WOOL, Material.ORANGE_WOOL, Material.LIGHT_GRAY_WOOL,
            Material.GRAY_WOOL, Material.BROWN_WOOL, Material.RED_WOOL, Material.PURPLE_WOOL,
            Material.BLUE_WOOL, Material.GREEN_WOOL, Material.CYAN_WOOL, Material.BLACK_WOOL));
    public static final ImmutableSet<Material> BEDS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_BED, Material.YELLOW_BED, Material.PINK_BED, Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED, Material.LIME_BED, Material.ORANGE_BED, Material.LIGHT_GRAY_BED,
            Material.GRAY_BED, Material.BROWN_BED, Material.RED_BED, Material.PURPLE_BED,
            Material.BLUE_BED, Material.GREEN_BED, Material.CYAN_BED, Material.BLACK_BED));
    public static final ImmutableSet<Material> BANNERS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_BANNER, Material.YELLOW_BANNER, Material.PINK_BANNER, Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER, Material.LIME_BANNER, Material.ORANGE_BANNER, Material.LIGHT_GRAY_BANNER,
            Material.GRAY_BANNER, Material.BROWN_BANNER, Material.RED_BANNER, Material.PURPLE_BANNER,
            Material.BLUE_BANNER, Material.GREEN_BANNER, Material.CYAN_BANNER, Material.BLACK_BANNER));
    public static final ImmutableSet<Material> BUTTONS = ImmutableSet.copyOf(EnumSet.of(
            Material.STONE_BUTTON, Material.OAK_BUTTON, Material.DARK_OAK_BUTTON,
            Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON,
            Material.SPRUCE_BUTTON));
}
