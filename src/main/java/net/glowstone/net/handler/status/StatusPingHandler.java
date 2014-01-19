package net.glowstone.net.handler.status;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.status.StatusPingMessage;

public final class StatusPingHandler implements MessageHandler<GlowSession, StatusPingMessage> {

    @Override
    public void handle(GlowSession session, StatusPingMessage message) {
        GlowServer.logger.info("Status ping, time: " + message.getTime());
        session.send(message);
    }
}
