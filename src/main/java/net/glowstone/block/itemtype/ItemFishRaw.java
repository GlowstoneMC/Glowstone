package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TickUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemFishRaw extends ItemFood {

    @Override
    protected float getSaturation(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
            case 1:
                return 0.4f;
            case 2:
            case 3:
                return 0.2f;
            default:
                throw new IllegalArgumentException("Cannot find fish(349) for data: " + data);
        }
    }

    @Override
    protected int getFoodLevel(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
            case 1:
                return 2;
            case 2:
            case 3:
                return 1;
            default:
                throw new IllegalArgumentException("Cannot find fish(349) for data: " + data);
        }
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        if (item.getData().getData() == 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                    TickUtil.minutesToTicks(1), 3), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                    TickUtil.secondsToTicks(15), 2), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,
                    TickUtil.secondsToTicks(15), 1), true);
        }
        return true;
    }

}
