package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.DynamicRecipe;
import org.bukkit.inventory.ItemStack;

public class GlowBannerCopyRecipe implements DynamicRecipe {

    ItemStack result = new ItemStack(Material.BANNER);

    /*
    - Must be exactly two banners
    - 1 with no pattern, and 1 with at least one layer
    - Must be same colour
    - No other items allowed in matrix
     */
    @Override
    public boolean match(ItemStack[] itemStacks) {
        return false;
    }

    @Override
    public ItemStack getResult() {
        return null;
    }
}
