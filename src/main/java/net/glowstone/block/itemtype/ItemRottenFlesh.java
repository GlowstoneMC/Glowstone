package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemRottenFlesh extends ItemFood {

    public ItemRottenFlesh() {
        super(4, 0.8f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        if (Math.random() < 0.8) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                TickUtil.secondsToTicks(30), 0), true);
        }
        return true;
    }
}
