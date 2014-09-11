package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import org.bukkit.GameMode;

public final class CreativeItemHandler implements MessageHandler<GlowSession, CreativeItemMessage> {
    @Override
    public void handle(GlowSession session, CreativeItemMessage message) {
        final GlowPlayer player = session.getPlayer();

        // only if creative mode
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.kickPlayer("Illegal creative mode item selection");
            return;
        }

        // only if default (player) inventory
        if (!GlowInventoryView.isDefault(player.getOpenInventory())) {
            player.kickPlayer("Illegal creative mode item selection");
            return;
        }

        if (message.getSlot() < 0) {
            // todo: drop outside
            return;
        }

        // todo: filter item for validity

        // in the creative inventory everything is handled client side
        player.getOpenInventory().setItem(message.getSlot(), message.getItem());
    }
}
