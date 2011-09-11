package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.PingMessage;
import net.glowstone.net.Session;

public class PingMessageHandler extends MessageHandler<PingMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, PingMessage message) {
        if (session.getPingMessageId() == message.getPingId()) {
            session.pong();
        }
    }
    
}
