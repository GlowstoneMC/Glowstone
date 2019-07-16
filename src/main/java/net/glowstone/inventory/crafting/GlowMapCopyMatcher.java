package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GlowMapCopyMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ItemStack original = null;
        int copies = 1;

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            switch (item.getType()) {
                case FILLED_MAP:
                    if (original != null) {
                        return null; // More than one original
                    }
                    original = item;
                    break;
                case MAP:
                    copies += 1;
                    break;
                default:
                    return null; // Non-map item
            }
        }

        if (original == null || copies == 1) {
            return null; // Not copying
        }

        int mapId = original.getDurability();

        return new ItemStack(Material.MAP, copies, (short) mapId);
    }
}
