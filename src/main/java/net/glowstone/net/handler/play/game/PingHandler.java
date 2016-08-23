package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PingPacket;

public final class PingHandler implements MessageHandler<GlowSession, PingPacket> {

    @Override
    public void handle(GlowSession session, PingPacket message) {
        session.pong(message.getPingId());
    }
}
