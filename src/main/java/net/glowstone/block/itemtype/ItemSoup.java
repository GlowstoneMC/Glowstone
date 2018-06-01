package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemSoup extends ItemFood {

    public ItemSoup(int foodLevel, float saturation) {
        super(foodLevel, saturation);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!handleEat(player, item)) {
            return false;
        }

        item.setType(Material.BOWL);
        return true;
    }
}
