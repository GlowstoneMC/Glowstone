package net.glowstone.inventory.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;

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
                case GUNPOWDER:
                    gunpowder++;
                    break;
                case FIREWORK_STAR:
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

        ItemStack ret = new ItemStack(Material.FIREWORK_ROCKET, 3);

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
            firework.addEffect(Objects.requireNonNull(charge.getEffect()));
        }

        ret.setItemMeta(firework);

        return ret;
    }
}
