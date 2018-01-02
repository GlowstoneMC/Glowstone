package net.glowstone.block.itemtype;

import org.bukkit.inventory.ItemStack;

public class ItemFishCooked extends ItemFood {

    @Override
    protected float getSaturation(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
                return 6f;
            case 1:
                return 9.6f;
            default:
                throw new IllegalArgumentException("Cannot find fish(350) for data: " + data);
        }
    }

    @Override
    protected int getFoodLevel(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
                return 5;
            case 1:
                return 6;
            default:
                throw new IllegalArgumentException("Cannot find fish(350) for data: " + data);
        }
    }
}
