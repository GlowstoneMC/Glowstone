package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GlowBookCopyMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ItemStack original = null;
        int copies = 0;

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            switch (item.getType()) {
                case WRITTEN_BOOK:
                    if (original != null) {
                        return null; // Only one original allowed
                    }
                    original = item;
                    break;
                case BOOK_AND_QUILL:
                    copies += 1;
                    break;
                default:
                    return null; // Wrong item in matrix
            }
        }

        if (original == null || copies == 0) {
            return null;
        }

        ItemStack ret = new ItemStack(Material.WRITTEN_BOOK, copies);
        ret.setItemMeta(original.getItemMeta());

        return ret;
    }

    //TODO: Keep old book in matrix
}
