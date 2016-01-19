package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ItemFood extends ItemTimedUsage {

    private int foodLevel;
    private float saturation;

    public ItemFood(int foodLevel, float saturation) {
        this.foodLevel = foodLevel;
        this.saturation = saturation;
    }

    public boolean eat(GlowPlayer player, ItemStack item) {

        FoodLevelChangeEvent foodLevelChangeEvent = new FoodLevelChangeEvent(player, foodLevel + player.getFoodLevel());
        EventFactory.callEvent(foodLevelChangeEvent);
        if (foodLevelChangeEvent.isCancelled()) return false;

        player.setFoodLevelAndSaturdation(foodLevelChangeEvent.getFoodLevel(), saturation);
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
