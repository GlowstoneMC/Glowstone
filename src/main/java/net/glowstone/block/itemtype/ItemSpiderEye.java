package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemSpiderEye extends ItemFood {

    public ItemSpiderEye() {
        super(2, 3.2f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                TickUtil.secondsToTicks(5), 0), true);
        return true;
    }
}
