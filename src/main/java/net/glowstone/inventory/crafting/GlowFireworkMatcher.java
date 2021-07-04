package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class GlowFireworkMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        int gunpowder = 0;
        boolean hasPaper = false;
        List<ItemStack> charges = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            switch (item.getType()) {
                case SULPHUR:
                    gunpowder++;
                    break;
                case FIREWORK_CHARGE:
                    charges.add(item);
                    break;
                case PAPER:
                    if (hasPaper) {
                        return null; // Only one paper allowed
                    }
                    hasPaper = true;
                    break;
                default:
                    return null; // Wrong item on matrix
            }
        }

        if (gunpowder < 1 || gunpowder > 3) {
            return null; // Must have 1-3 gunpowder
        }
        if (!hasPaper) {
            return null; // Paper needed
        }

        ItemStack ret = new ItemStack(Material.FIREWORK, 3);

        if (charges.isEmpty()) { // This makes no sense Mojang, but whatever
            return ret;
        }

        FireworkMeta firework = (FireworkMeta) ret.getItemMeta();

        firework.setPower(gunpowder);
        for (ItemStack item : charges) {
            FireworkEffectMeta charge = (FireworkEffectMeta) item.getItemMeta();
            if (!charge.hasEffect()) {
                continue;
            }
            firework.addEffect(charge.getEffect());
        }

        ret.setItemMeta(firework);

        return ret;
    }
}
