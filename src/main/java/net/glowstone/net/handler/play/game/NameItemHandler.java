package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowAnvilInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.NameItemMessage;

public final class NameItemHandler implements MessageHandler<GlowSession, NameItemMessage> {
    @Override
    public void handle(GlowSession session, NameItemMessage message) {
        GlowPlayer player = session.getPlayer();

        // Verify that the current player is in an anvil
        if (!(player.getOpenInventory().getTopInventory() instanceof GlowAnvilInventory)) {
            return;
        }

        GlowAnvilInventory anvil = (GlowAnvilInventory) player.getOpenInventory().getTopInventory();
        anvil.setRenameText(message.getName());
    }
}
