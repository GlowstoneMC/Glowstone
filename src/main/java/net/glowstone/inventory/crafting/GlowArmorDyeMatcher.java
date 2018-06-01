package net.glowstone.inventory.crafting;

import java.util.ArrayList;
import java.util.List;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Dye;

public class GlowArmorDyeMatcher extends ItemMatcher {

    public static final MaterialMatcher LEATHERS = material ->
        material.equals(Material.LEATHER_HELMET)
            || material.equals(Material.LEATHER_CHESTPLATE)
            || material.equals(Material.LEATHER_LEGGINGS)
            || material.equals(Material.LEATHER_BOOTS);

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ItemStack armor = null;
        List<Color> colors = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            if (item.getType() == Material.INK_SACK) {
                Color color = ((Dye) item.getData()).getColor().getColor();
                colors.add(color);
                continue;
            }

            if (LEATHERS.matches(item.getType())) {
                if (armor != null) {
                    return null; // Can't dye more than one item
                }
                armor = item;
                continue;
            }

            return null; // Non-armor item
        }

        if (armor == null) {
            return null; // No armor
        }
        if (colors.isEmpty()) {
            return null; // No colors
        }

        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        Color base = meta.getColor();
        if (meta.getColor() == GlowItemFactory.instance().getDefaultLeatherColor()) {
            base = colors.remove(0);
        }

        Color newColor = base.mixColors(colors.toArray(new Color[colors.size()]));

        ItemStack ret = armor.clone();
        LeatherArmorMeta retMeta = (LeatherArmorMeta) ret.getItemMeta();
        retMeta.setColor(newColor);
        ret.setItemMeta(retMeta);

        return ret;
    }
}
