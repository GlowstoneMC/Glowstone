package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class ItemPoisonousPotato extends ItemFood {

    public ItemPoisonousPotato() {
        super(1, 0.6f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) return false;

        if (Math.random() < 0.6) {
            player.addPotionEffect(PotionEffectType.POISON.createEffect(4 * 20, 1), true);
        }
        return true;
    }
}
