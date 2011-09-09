package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.CloseWindowMessage;
import net.glowstone.net.Session;
import org.bukkit.GameMode;

public final class CloseWindowMessageHandler extends MessageHandler<CloseWindowMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, CloseWindowMessage message) {
        if (player == null)
            return;
        
        if (player.getItemOnCursor() != null) {
            // player.getWorld().dropItem(player.getEyeLocation(), player.getItemInHand());
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getInventory().addItem(player.getItemOnCursor());
            }
            player.setItemOnCursor(null);
        }
    }
    
}
