package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.PickItemMessage;

public final class PickItemHandler implements MessageHandler<GlowSession, PickItemMessage> {
    @Override
    public void handle(GlowSession session, PickItemMessage pickItemMessage) {
        //TODO: handle pick item
    }
}
