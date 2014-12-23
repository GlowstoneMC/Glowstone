package net.glowstone.block.block2.types;

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

    public TreeSpecies getSpecies() {
        return TreeSpecies.values()[ordinal()];
    }

    public static TreeVariant fromSpecies(TreeSpecies species) {
        return values()[species.ordinal()];
    }
}
