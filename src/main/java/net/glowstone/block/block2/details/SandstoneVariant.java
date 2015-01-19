package net.glowstone.block.block2.details;

import org.bukkit.SandstoneType;
import org.bukkit.StoneType;

/**
 * Todo: Javadoc for SandstoneVariant.
 */
public enum SandstoneVariant {
    SANDSTONE,
    CHISELED_SANDSTONE,
    SMOOTH_SANDSTONE;

    public SandstoneType toSandstoneType() {
        return SandstoneType.values()[ordinal()];
    }

    public static SandstoneVariant fromSandstoneType(StoneType type) {
        return values()[type.ordinal()];
    }
}
