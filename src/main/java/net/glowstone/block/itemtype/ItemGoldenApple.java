package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple() {
        super(4, 9.6f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        byte data = item.getData().getData();
        if (data == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2 * 60 * 20, 0),
                true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1), true);
        } else if (data == 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2 * 60 * 20, 3),
                true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1), true);
            player.addPotionEffect(
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 60 * 20, 0), true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 60 * 20, 0),
                    true);
        }

        return true;
    }
}
