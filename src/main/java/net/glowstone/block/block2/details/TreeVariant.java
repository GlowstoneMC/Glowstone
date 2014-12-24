package net.glowstone.block.block2.details;

import org.bukkit.TreeSpecies;

/**
 * Available variants for tree types.
 */
public enum TreeVariant {
    OAK,
    SPRUCE,
    BIRCH,
    JUNGLE,
    ACACIA,
    DARK_OAK;

    public static final TreeVariant[] FIRST_HALF = {OAK, SPRUCE, BIRCH, JUNGLE};
    public static final TreeVariant[] SECOND_HALF = {ACACIA, DARK_OAK};

    public TreeSpecies toSpecies() {
        return TreeSpecies.values()[ordinal()];
    }

    public static TreeVariant fromSpecies(TreeSpecies species) {
        return values()[species.ordinal()];
    }
}
