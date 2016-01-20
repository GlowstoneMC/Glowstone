package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple() {
        super(4, 9.6f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) return false;

        player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(2 * 60 * 20, 1), true);

        byte data = item.getData().getData();
        if (data == 0) {
            player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(5 * 20, 2), true);
        } else if (data == 1) {
            player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(30 * 20, 5), true);
            player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(5 * 60 * 20, 1), true);
            player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(5 * 60 * 20, 1), true);
        }

        return true;
    }
}
