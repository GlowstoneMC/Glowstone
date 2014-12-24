package net.glowstone.block.block2.details;

import org.bukkit.StoneType;

/**
 * Available variants for "stone".
 */
public enum StoneVariant {
    STONE,
    GRANITE,
    SMOOTH_GRANITE,
    DIORITE,
    SMOOTH_DIORITE,
    ANDESITE,
    SMOOTH_ANDESITE;

    public StoneType toStoneType() {
        return StoneType.values()[ordinal()];
    }

    public static StoneVariant fromStoneType(StoneType type) {
        return values()[type.ordinal()];
    }
}
