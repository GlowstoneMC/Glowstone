package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemPoisonousPotato extends ItemFood {

    public ItemPoisonousPotato() {
        super(2, 1.2f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        if (Math.random() < 0.6) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 0), true);
        }
        return true;
    }
}
