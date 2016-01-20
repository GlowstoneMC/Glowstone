package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ItemFood extends ItemTimedUsage {

    private int foodLevel;
    private float saturation;

    public ItemFood(int foodLevel, float saturation) {
        this.foodLevel = foodLevel;
        this.saturation = saturation;
    }

    ItemFood() { // class should override getFoodLevel and getSaturation
        this.foodLevel = 0;
        this.saturation = 0;
    }

    protected int getFoodLevel(ItemStack stack) {
        return foodLevel;
    }

    protected float getSaturation(ItemStack stack) {
        return saturation;
    }

    public boolean eat(GlowPlayer player, ItemStack item) {

        PlayerItemConsumeEvent event1 = new PlayerItemConsumeEvent(player, item);
        EventFactory.callEvent(event1);
        if (event1.isCancelled()) return false;

        FoodLevelChangeEvent event2 = new FoodLevelChangeEvent(player, getFoodLevel(item) + player.getFoodLevel());
        EventFactory.callEvent(event2);

        if (!event2.isCancelled()) {
            player.setFoodLevelAndSaturation(event2.getFoodLevel(), getSaturation(item));
        }

        player.setUsageItem(null);
        player.setUsageTime(0);
        item.setAmount(item.getAmount() - 1);
        return true;
    }

    @Override
    public void startUse(GlowPlayer player, ItemStack item) {
        player.setUsageItem(item);
        player.setUsageTime(39);
    }

    @Override
    public void endUse(GlowPlayer player, ItemStack item) {
        player.setUsageItem(null);
        player.setUsageTime(0);
    }
}
