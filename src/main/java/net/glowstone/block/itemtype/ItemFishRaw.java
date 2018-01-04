package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60 * 20, 3), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 15 * 20, 2), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 15 * 20, 1), true);
        }
        return true;
    }

}
