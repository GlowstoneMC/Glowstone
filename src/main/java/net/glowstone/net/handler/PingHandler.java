package net.glowstone.net.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.game.PingMessage;

public class PingHandler extends MessageHandler<PingMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, PingMessage message) {
        session.pong(message.getPingId());
    }
}
