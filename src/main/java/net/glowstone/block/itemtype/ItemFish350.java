package net.glowstone.block.itemtype;

import org.bukkit.inventory.ItemStack;

public class ItemFish350 extends ItemFood {

    @Override
    protected float getSaturation(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
                return 0.4f;
            case 1:
                return 9.6f;
        }
        throw new IllegalArgumentException("Cannot find fish(350) for data: " + data);
    }

    @Override
    protected int getFoodLevel(ItemStack stack) {
        byte data = stack.getData().getData();
        switch (data) {
            case 0:
                return 2;
            case 1:
                return 6;
        }
        throw new IllegalArgumentException("Cannot find fish(350) for data: " + data);
    }
}
