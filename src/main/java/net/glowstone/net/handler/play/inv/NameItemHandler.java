package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.NameItemMessage;

public final class NameItemHandler implements MessageHandler<GlowSession, NameItemMessage> {
    @Override
    public void handle(GlowSession session, NameItemMessage message) {
        //TODO: handle packet
    }
}
