package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

public final class CreativeItemHandler implements MessageHandler<GlowSession, CreativeItemMessage> {
    public void handle(GlowSession session, CreativeItemMessage message) {
        final GlowPlayer player = session.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.kickPlayer("Illegal creative mode item selection");
            return;
        }
        GlowInventory inv = player.getInventory();
        int slot = inv.getItemSlot(message.getSlot());

        if (slot < 0 || slot > 8) {
            return;
        }
        ItemStack newItem = message.getItem();
        GlowItemStack currentItem = inv.getItem(slot);

        inv.setItem(slot, newItem);
        if (currentItem != null) {
            player.setItemOnCursor(currentItem);
        } else {
            player.setItemOnCursor(null);
        }
    }
}
