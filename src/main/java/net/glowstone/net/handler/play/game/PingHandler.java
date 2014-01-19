package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PingMessage;

public final class PingHandler implements MessageHandler<GlowSession, PingMessage> {

    @Override
    public void handle(GlowSession session, PingMessage message) {
        session.pong(message.getPingId());
    }
}
