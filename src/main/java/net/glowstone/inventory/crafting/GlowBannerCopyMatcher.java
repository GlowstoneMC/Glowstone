package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.DynamicRecipe;
import org.bukkit.inventory.ItemMatcher;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GlowBannerCopyMatcher extends ItemMatcher {

    ItemStack result = new ItemStack(Material.BANNER);

    /*
    - Must be exactly two banners
    - 1 with no pattern, and 1 with at least one layer
    - Must be same colour
    - No other items allowed in matrix
     */
    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ArrayList<ItemStack> banners = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) continue;

            if (item.getType() == Material.BANNER) {
                banners.add(item);
                continue;
            }


        }
        return null;
    }
}
