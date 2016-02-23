package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ItemMilk extends ItemFood {

    public ItemMilk() {
        super(0, 0);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        PlayerItemConsumeEvent event1 = new PlayerItemConsumeEvent(player, item);
        EventFactory.callEvent(event1);
        if (event1.isCancelled()) return false;

        // todo implement playeritemconsumeevent set item

        player.setUsageItem(null);
        player.setUsageTime(0);
        player.getInventory().getItemInHand().setType(Material.BUCKET);

        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        return true;
    }
}
