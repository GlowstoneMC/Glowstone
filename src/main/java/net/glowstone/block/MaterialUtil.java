package net.glowstone.block;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Predicate;
import org.bukkit.Material;

/**
 * Useful constant groups of materials, many of which were just 1 or 2 materials pre-Flattening.
 */
public class MaterialUtil {
    public static final ImmutableSet<Material> WOOLS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_WOOL, Material.YELLOW_WOOL, Material.PINK_WOOL, Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL, Material.LIME_WOOL, Material.ORANGE_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.GRAY_WOOL, Material.BROWN_WOOL, Material.RED_WOOL, Material.PURPLE_WOOL,
            Material.BLUE_WOOL, Material.GREEN_WOOL, Material.CYAN_WOOL, Material.BLACK_WOOL));
    public static final ImmutableSet<Material> BEDS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_BED, Material.YELLOW_BED, Material.PINK_BED, Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED, Material.LIME_BED, Material.ORANGE_BED,
            Material.LIGHT_GRAY_BED,
            Material.GRAY_BED, Material.BROWN_BED, Material.RED_BED, Material.PURPLE_BED,
            Material.BLUE_BED, Material.GREEN_BED, Material.CYAN_BED, Material.BLACK_BED));
    public static final ImmutableSet<Material> BANNERS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_BANNER, Material.YELLOW_BANNER, Material.PINK_BANNER,
            Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER, Material.LIME_BANNER, Material.ORANGE_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.GRAY_BANNER, Material.BROWN_BANNER, Material.RED_BANNER,
            Material.PURPLE_BANNER,
            Material.BLUE_BANNER, Material.GREEN_BANNER, Material.CYAN_BANNER,
            Material.BLACK_BANNER));
    public static final ImmutableSet<Material> CARPETS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_CARPET, Material.YELLOW_CARPET, Material.PINK_CARPET,
            Material.MAGENTA_CARPET,
            Material.LIGHT_BLUE_CARPET, Material.LIME_CARPET, Material.ORANGE_CARPET,
            Material.LIGHT_GRAY_CARPET,
            Material.GRAY_CARPET, Material.BROWN_CARPET, Material.RED_CARPET,
            Material.PURPLE_CARPET,
            Material.BLUE_CARPET, Material.GREEN_CARPET, Material.CYAN_CARPET,
            Material.BLACK_CARPET));
    public static final ImmutableSet<Material> CONCRETE = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE, Material.PINK_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE, Material.LIME_CONCRETE, Material.ORANGE_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE,
            Material.GRAY_CONCRETE, Material.BROWN_CONCRETE, Material.RED_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.BLUE_CONCRETE, Material.GREEN_CONCRETE, Material.CYAN_CONCRETE,
            Material.BLACK_CONCRETE));
    public static final ImmutableSet<Material> CONCRETE_POWDER = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.CYAN_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER));
    public static final ImmutableSet<Material> UNGLAZED_TERRACOTTA =
            ImmutableSet.copyOf(EnumSet.of(Material.TERRACOTTA,
                    Material.WHITE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.PINK_TERRACOTTA,
                    Material.MAGENTA_TERRACOTTA,
                    Material.LIGHT_BLUE_TERRACOTTA, Material.LIME_TERRACOTTA,
                    Material.ORANGE_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
                    Material.GRAY_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.RED_TERRACOTTA,
                    Material.PURPLE_TERRACOTTA,
                    Material.BLUE_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.CYAN_TERRACOTTA,
                    Material.BLACK_TERRACOTTA));
    public static final ImmutableSet<Material> GLAZED_TERRACOTTA = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.GRAY_GLAZED_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA));
    public static final ImmutableSet<Material> STAINED_GLASS_BLOCKS = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS,
            Material.PINK_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS, Material.LIME_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS, Material.BROWN_STAINED_GLASS,
            Material.RED_STAINED_GLASS, Material.PURPLE_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS, Material.GREEN_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS, Material.BLACK_STAINED_GLASS));
    public static final ImmutableSet<Material> STAINED_GLASS_PANES = ImmutableSet.copyOf(EnumSet.of(
            Material.WHITE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE));
    public static final ImmutableSet<Material> WOODEN_BUTTONS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_BUTTON, Material.DARK_OAK_BUTTON,
            Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON,
            Material.SPRUCE_BUTTON));
    public static final ImmutableSet<Material> BUTTONS = allMaterialsEndingWith("BUTTON");
    /**
     * Blocks that spawn silverfish when broken.
     */
    public static final ImmutableSet<Material> INFESTED = ImmutableSet.copyOf(EnumSet.of(
            Material.INFESTED_STONE, Material.INFESTED_COBBLESTONE,
            Material.INFESTED_CHISELED_STONE_BRICKS,
            Material.INFESTED_CRACKED_STONE_BRICKS, Material.INFESTED_MOSSY_STONE_BRICKS,
            Material.INFESTED_STONE_BRICKS));
    public static final ImmutableSet<Material> STANDING_HEADS = ImmutableSet.copyOf(EnumSet.of(
            Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD,
            Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL));
    public static final ImmutableSet<Material> WALL_HEADS = ImmutableSet.copyOf(EnumSet.of(
            Material.CREEPER_WALL_HEAD, Material.DRAGON_WALL_HEAD, Material.PLAYER_WALL_HEAD,
            Material.ZOMBIE_WALL_HEAD, Material.SKELETON_WALL_SKULL,
            Material.WITHER_SKELETON_WALL_SKULL));
    public static final ImmutableSet<Material> SPAWN_EGGS = allMaterialsEndingWith("SPAWN_EGG");
    public static final ImmutableSet<Material> LOGS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_LOG, Material.DARK_OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.SPRUCE_LOG));
    public static final ImmutableSet<Material> WOODS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_WOOD, Material.DARK_OAK_WOOD, Material.ACACIA_WOOD, Material.BIRCH_WOOD,
            Material.JUNGLE_WOOD, Material.SPRUCE_WOOD));
    public static final ImmutableSet<Material> LEAVES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_LEAVES, Material.DARK_OAK_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES, Material.SPRUCE_LEAVES));
    public static final ImmutableSet<Material> WOODEN_STAIRS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_STAIRS, Material.DARK_OAK_STAIRS, Material.ACACIA_STAIRS,
            Material.BIRCH_STAIRS,
            Material.JUNGLE_STAIRS, Material.SPRUCE_STAIRS));
    public static final ImmutableSet<Material> STAIRS = allMaterialsEndingWith("STAIRS");
    public static final ImmutableSet<Material> WOODEN_SLABS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_SLAB, Material.DARK_OAK_SLAB, Material.ACACIA_SLAB, Material.BIRCH_SLAB,
            Material.JUNGLE_SLAB, Material.SPRUCE_SLAB));
    public static final ImmutableSet<Material> SLABS = allMaterialsEndingWith("SLAB");
    public static final ImmutableSet<Material> SAPLINGS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_SAPLING, Material.DARK_OAK_SAPLING, Material.ACACIA_SAPLING,
            Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING, Material.SPRUCE_SAPLING));
    public static final ImmutableSet<Material> BOATS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_BOAT, Material.DARK_OAK_BOAT, Material.ACACIA_BOAT, Material.BIRCH_BOAT,
            Material.JUNGLE_BOAT, Material.SPRUCE_BOAT));
    public static final ImmutableSet<Material> WOODEN_DOORS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_DOOR, Material.DARK_OAK_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR, Material.SPRUCE_DOOR));
    public static final ImmutableSet<Material> WOODEN_FENCES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_FENCE, Material.DARK_OAK_FENCE, Material.ACACIA_FENCE,
            Material.BIRCH_FENCE,
            Material.JUNGLE_FENCE, Material.SPRUCE_FENCE));
    public static final ImmutableSet<Material> WOODEN_GATES = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE));
    public static final ImmutableSet<Material> TRAPDOORS = ImmutableSet.copyOf(EnumSet.of(
            Material.OAK_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.SPRUCE_TRAPDOOR));
    public static final ImmutableSet<Material> WOODEN_PRESSURE_PLATES =
            ImmutableSet.copyOf(EnumSet.of(
                    Material.OAK_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE,
                    Material.ACACIA_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE,
                    Material.JUNGLE_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE));

    // 1.14 adds Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.WITHER_ROSE
    public static final ImmutableSet<Material> OVERWORLD_FLOWERS = ImmutableSet.copyOf(EnumSet.of(
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY));

    private static ImmutableSet<Material> allMaterialsEndingWith(String suffix) {
        return allMaterialsThat(material -> material.name().endsWith(suffix));
    }

    private static ImmutableSet<Material> allMaterialsThat(Predicate<Material> predicate) {
        EnumSet<Material> builder = EnumSet.noneOf(Material.class);
        Arrays.stream(Material.values()).filter(predicate).forEach(builder::add);
        return ImmutableSet.copyOf(builder);
    }
}
