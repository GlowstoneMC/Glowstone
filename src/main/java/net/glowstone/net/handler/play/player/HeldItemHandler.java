package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.HeldItemMessage;

public final class HeldItemHandler implements MessageHandler<GlowSession, HeldItemMessage> {
    public void handle(GlowSession session, HeldItemMessage message) {
        final int slot = message.getSlot();
        if (slot < 0 || slot > 8) // sanity check
            return;
        session.getPlayer().getInventory().setHeldItemSlot(slot);
    }
}
