package net.glowstone.inventory.crafting;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class GlowBannerCopyMatcher extends ItemMatcher {

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
            if (item == null) {
                continue;
            }

            // TODO: handle all new banner types
            if (item.getType() == Material.LEGACY_BANNER) {
                banners.add(item);
                continue;
            }
            return null; // Non-banner item in matrix
        }

        if (banners.size() != 2) {
            return null; // Must have 2 banners only
        }

        if (banners.get(0).getDurability() != banners.get(1).getDurability()) {
            return null; // Not same color
        }

        ItemStack original = null;
        ItemStack blank = null;

        for (ItemStack banner : banners) {
            BannerMeta meta = (BannerMeta) banner.getItemMeta();
            if (meta.getPatterns().isEmpty()) {
                if (blank != null) {
                    return null; // More than 1 blank
                }
                blank = banner;
            } else {
                if (original != null) {
                    return null; // More than 1 original
                }
                original = banner;
            }
        }

        if (original == null || blank == null) {
            return null; // Haven't got both needed banners
        }

        return original.clone();
    }

    //TODO: Keep banner in matrix after crafting

}
