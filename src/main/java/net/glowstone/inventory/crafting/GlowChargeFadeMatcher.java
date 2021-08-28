package net.glowstone.inventory.crafting;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.List;

public class GlowChargeFadeMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        ItemStack charge = null;
        List<Color> colors = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            switch (item.getType()) {
                case INK_SAC:
                    Dye dye = (Dye) item.getData();
                    colors.add(dye.getColor().getFireworkColor());
                    break;
                case FIREWORK_STAR:
                    charge = item;
                    break;
                default:
                    return null; // Wrong item on matrix
            }
        }

        if (charge == null || colors.isEmpty()) {
            return null; // No charge, or no colors
        }

        FireworkEffectMeta meta = (FireworkEffectMeta) charge.getItemMeta();
        FireworkEffect old = meta.getEffect();

        if (old == null) {
            return null;
        }

        FireworkEffect newEffect = FireworkEffect.builder()
            .with(old.getType())
            .withColor(old.getColors())
            .flicker(old.hasFlicker())
            .trail(old.hasTrail())
            .withFade(colors)
            .build();

        meta.setEffect(newEffect);

        ItemStack ret = charge.clone();
        ret.setItemMeta(meta);

        return ret;
    }
}
