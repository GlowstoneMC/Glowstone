package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                TickUtil.minutesToTicks(2), 0), true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                    TickUtil.secondsToTicks(5), 1), true);
        } else if (data == 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                TickUtil.minutesToTicks(2), 3), true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                    TickUtil.secondsToTicks(20), 1), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                TickUtil.minutesToTicks(5), 0), true);
            player
                .addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
                    TickUtil.minutesToTicks(5), 0), true);
        }

        return true;
    }
}
