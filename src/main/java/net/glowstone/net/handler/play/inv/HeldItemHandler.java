package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.HeldItemMessage;

public final class HeldItemHandler implements MessageHandler<GlowSession, HeldItemMessage> {
    @Override
    public void handle(GlowSession session, HeldItemMessage message) {
        final int slot = message.getSlot();
        if (slot < 0 || slot > 8) // sanity check
            return;
        session.getPlayer().getInventory().setRawHeldItemSlot(slot);
    }
}
