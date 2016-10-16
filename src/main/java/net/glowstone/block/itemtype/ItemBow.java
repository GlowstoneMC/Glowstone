package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

public class ItemBow extends ItemTimedUsage {

    @Override
    public void startUse(GlowPlayer player, ItemStack item) {
        player.setUsageItem(item);
        player.setUsageTime(20);
    }

    @Override
    public void endUse(GlowPlayer player, ItemStack item) {
        player.launchProjectile(Arrow.class);
        player.setUsageItem(null);
        player.setUsageTime(0);
    }
}
