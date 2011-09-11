package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.msg.QuickBarMessage;
import net.glowstone.net.Session;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QuickBarMessageHandler extends MessageHandler<QuickBarMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, QuickBarMessage message) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.kickPlayer("Now now, don't try that here. Won't work.");
            return;
        }
        GlowInventory inv = player.getInventory();
        int slot = inv.getItemSlot(message.getSlot());

        if (slot < 0 || slot > 8
                || Material.getMaterial(message.getId()) == null) {
            return;
        }
        ItemStack newItem = new ItemStack(message.getId(), message.getAmount(), message.getDamage());
        ItemStack currentItem = inv.getItem(slot);

        inv.setItem(slot, newItem);
        if (currentItem != null) {
            player.setItemOnCursor(currentItem);
        } else {
            player.setItemOnCursor(null);
        }
    }
    
}
