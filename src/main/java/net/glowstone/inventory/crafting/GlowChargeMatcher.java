package net.glowstone.inventory.crafting;

import static org.bukkit.FireworkEffect.Type;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.material.Dye;

public class GlowChargeMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        boolean hasGunpowder = false;
        boolean trail = false;
        boolean twinkle = false;
        List<Color> colors = new ArrayList<>();
        Type type = Type.BALL;

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            switch (item.getType()) {
                case SULPHUR:
                    if (hasGunpowder) {
                        return null; // Only 1 gunpowder allowed
                    }
                    hasGunpowder = true;
                    break;
                case INK_SACK:
                    Dye dye = (Dye) item.getData();
                    colors.add(dye.getColor().getFireworkColor());
                    break;
                case DIAMOND:
                    if (trail) {
                        return null; // Only 1 diamond allowed
                    }
                    trail = true;
                    break;
                case GLOWSTONE_DUST:
                    if (twinkle) {
                        return null; // Only 1 dust allowed
                    }
                    twinkle = true;
                    break;
                case FIREBALL:
                    if (type != Type.BALL) {
                        return null;
                    }
                    type = Type.BALL_LARGE;
                    break;
                case GOLD_NUGGET:
                    if (type != Type.BALL) {
                        return null;
                    }
                    type = Type.STAR;
                    break;
                case FEATHER:
                    if (type != Type.BALL) {
                        return null;
                    }
                    type = Type.BURST;
                    break;
                case SKULL_ITEM:
                    if (type != Type.BALL) {
                        return null;
                    }
                    type = Type.CREEPER;
                    break;
                default:
                    return null; // Non firework item on matrix
            }
        }

        if (!hasGunpowder || colors.isEmpty()) {
            return null; // Not enough ingredients
        }

        FireworkEffect effect = FireworkEffect.builder()
            .withColor(colors)
            .trail(trail)
            .flicker(twinkle)
            .with(type)
            .build();

        ItemStack charge = new ItemStack(Material.FIREWORK_CHARGE, 1);
        FireworkEffectMeta meta = (FireworkEffectMeta) charge.getItemMeta();
        meta.setEffect(effect);
        charge.setItemMeta(meta);

        return charge;
    }
}
