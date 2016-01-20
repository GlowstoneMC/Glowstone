package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class ItemFish349 extends ItemFood {

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
        }
        throw new IllegalArgumentException("Cannot find fish(349) for data: " + data);
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
        }
        throw new IllegalArgumentException("Cannot find fish(349) for data: " + data);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) return false;

        if (item.getData().getData() == 3) {
            player.addPotionEffect(PotionEffectType.POISON.createEffect(60 * 20, 4), true);
            player.addPotionEffect(PotionEffectType.HUNGER.createEffect(15 * 20, 3), true);
            player.addPotionEffect(PotionEffectType.CONFUSION.createEffect(15 * 20, 2), true);
        }
        return true;
    }

}
