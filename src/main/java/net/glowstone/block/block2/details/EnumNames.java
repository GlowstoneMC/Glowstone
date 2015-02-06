package net.glowstone.block.block2.details;

import com.google.common.collect.ImmutableMap;
import org.bukkit.*;

import java.util.Map;

/**
 * Enum name mappings.
 */
public final class EnumNames {

    private EnumNames() {
    }

    public static Map<DirtType, String> dirt() {
        return ImmutableMap.of(
                DirtType.NORMAL, "dirt",
                DirtType.COARSE, "coarse_dirt",
                DirtType.PODZOL, "podzol");
    }

    public static Map<SandType, String> sand() {
        return ImmutableMap.of(
                SandType.NORMAL, "sand",
                SandType.RED, "red_sand");
    }

    public static Map<SandstoneType, String> sandstone() {
        return ImmutableMap.of(
                SandstoneType.CRACKED, "sandstone",
                SandstoneType.GLYPHED, "chiseled_sandstone",
                SandstoneType.SMOOTH, "smooth_sandstone");
    }

    public static Map<StoneType, String> stone() {
        return ImmutableMap.<StoneType, String>builder()
                .put(StoneType.NORMAL, "stone")
                .put(StoneType.GRANITE, "granite")
                .put(StoneType.POLISHED_GRANITE, "smooth_granite")
                .put(StoneType.DIORITE, "diorite")
                .put(StoneType.POLISHED_DIORITE, "smooth_diorite")
                .put(StoneType.ANDESITE, "andesite")
                .put(StoneType.POLISHED_ANDESITE, "smooth_andesite")
                .build();
    }

    public static Map<TreeSpecies, String> tree() {
        return ImmutableMap.<TreeSpecies, String>builder()
                .put(TreeSpecies.GENERIC, "oak")
                .put(TreeSpecies.REDWOOD, "spruce")
                .put(TreeSpecies.BIRCH, "birch")
                .put(TreeSpecies.JUNGLE, "jungle")
                .put(TreeSpecies.ACACIA, "acacia")
                .put(TreeSpecies.DARK_OAK, "dark_oak")
                .build();
    }

    public static Map<GrassSpecies, String> grass() {
        return ImmutableMap.of(
                GrassSpecies.DEAD, "dead_bush",
                GrassSpecies.NORMAL, "tall_grass",
                GrassSpecies.FERN_LIKE, "fern");
    }

    public static Map<DyeColor, String> color() {
        ImmutableMap.Builder<DyeColor, String> builder = ImmutableMap.builder();
        for (DyeColor value : DyeColor.values()) {
            if (value == DyeColor.LIGHT_BLUE) {
                builder.put(value, "lightBlue");
            } else {
                builder.put(value, value.name().toLowerCase());
            }
        }
        return builder.build();
    }
}
