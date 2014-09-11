package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.CloseWindowMessage;
import org.bukkit.GameMode;

public final class CloseWindowHandler implements MessageHandler<GlowSession, CloseWindowMessage> {
    @Override
    public void handle(GlowSession session, CloseWindowMessage message) {
        final GlowPlayer player = session.getPlayer();

        // todo: drop items from workbench, enchant inventory, own crafting grid if needed

        player.closeInventory();

        if (player.getItemOnCursor() != null) {
            // player.getWorld().dropItem(player.getEyeLocation(), player.getItemInHand());
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getInventory().addItem(player.getItemOnCursor());
            }
            player.setItemOnCursor(null);
        }
    }
}
