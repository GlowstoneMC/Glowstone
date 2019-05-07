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
    public static final ImmutableSet<Material> CARPETS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_CARPET, Material.YELLOW_CARPET, Material.PINK_CARPET, Material.MAGENTA_CARPET,
            Material.LIGHT_BLUE_CARPET, Material.LIME_CARPET, Material.ORANGE_CARPET, Material.LIGHT_GRAY_CARPET,
            Material.GRAY_CARPET, Material.BROWN_CARPET, Material.RED_CARPET, Material.PURPLE_CARPET,
            Material.BLUE_CARPET, Material.GREEN_CARPET, Material.CYAN_CARPET, Material.BLACK_CARPET));
    public static final ImmutableSet<Material> WOODEN_BUTTONS;
    public static final ImmutableSet<Material> BUTTONS;
    static {
        EnumSet<Material> buttonsBuilder = EnumSet.of(
            Material.OAK_BUTTON, Material.DARK_OAK_BUTTON,
            Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON,
            Material.SPRUCE_BUTTON);
        WOODEN_BUTTONS = ImmutableSet.copyOf(buttonsBuilder);
        buttonsBuilder.add(Material.STONE_BUTTON);
        BUTTONS = ImmutableSet.copyOf(buttonsBuilder);
    }
    public static final ImmutableSet<Material> LOGS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_LOG, Material.DARK_OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.SPRUCE_LOG));
    public static final ImmutableSet<Material> WOODS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_WOOD, Material.DARK_OAK_WOOD, Material.ACACIA_WOOD, Material.BIRCH_WOOD,
            Material.JUNGLE_WOOD, Material.SPRUCE_WOOD));
    public static final ImmutableSet<Material> WOODEN_STAIRS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_STAIRS, Material.DARK_OAK_STAIRS, Material.ACACIA_STAIRS, Material.BIRCH_STAIRS,
            Material.JUNGLE_STAIRS, Material.SPRUCE_STAIRS));
    public static final ImmutableSet<Material> WOODEN_SLABS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_SLAB, Material.DARK_OAK_SLAB, Material.ACACIA_SLAB, Material.BIRCH_SLAB,
            Material.JUNGLE_SLAB, Material.SPRUCE_SLAB));
    public static final ImmutableSet<Material> SAPLINGS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_SAPLING, Material.DARK_OAK_SAPLING, Material.ACACIA_SAPLING, Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING, Material.SPRUCE_SAPLING));
    public static final ImmutableSet<Material> BOATS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_BOAT, Material.DARK_OAK_BOAT, Material.ACACIA_BOAT, Material.BIRCH_BOAT,
            Material.JUNGLE_BOAT, Material.SPRUCE_BOAT));
    public static final ImmutableSet<Material> WOODEN_FENCES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_FENCE, Material.DARK_OAK_FENCE, Material.ACACIA_FENCE, Material.BIRCH_FENCE,
            Material.JUNGLE_FENCE, Material.SPRUCE_FENCE));
    public static final ImmutableSet<Material> WOODEN_GATES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE));
    public static final ImmutableSet<Material> TRAPDOORS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.SPRUCE_TRAPDOOR));
    public static final ImmutableSet<Material> WOODEN_PRESSURE_PLATES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE));
}
