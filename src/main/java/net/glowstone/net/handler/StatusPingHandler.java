package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.status.StatusPingMessage;

public class StatusPingHandler extends MessageHandler<StatusPingMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, StatusPingMessage message) {
        GlowServer.logger.info("Status ping, time: " + message.getTime());
        session.send(message);
    }
}
