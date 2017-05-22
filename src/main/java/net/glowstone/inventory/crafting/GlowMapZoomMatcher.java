package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GlowMapZoomMatcher extends ItemMatcher {

    public static final char PAPER = '#';
    public static final char MAP = 'X';
    private static final String RECIPE = "###" + "#X#" + "###";

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ItemStack original = null;

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null) {
                switch (RECIPE.charAt(i)) {
                    case PAPER:
                        if (matrix[i].getType() != Material.PAPER) return null;
                        break;
                    case MAP:
                        if (matrix[i].getType() != Material.MAP) return null;
                        original = matrix[i];
                        break;
                    default:
                        return null; // Does not match recipe
                }
            }
        }

        if (original == null) return null; // No map

        //TODO: Add zooming once maps are implemented

        return original.clone();
    }
}
