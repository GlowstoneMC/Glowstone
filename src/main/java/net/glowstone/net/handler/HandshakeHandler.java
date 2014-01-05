package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.ProtocolState;
import net.glowstone.net.Session;
import net.glowstone.net.message.HandshakeMessage;

public class HandshakeHandler extends MessageHandler<HandshakeMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, HandshakeMessage message) {
        if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
            session.disconnect("Outdated client!");
            return;
        } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
            session.disconnect("Outdated server!");
            return;
        }

        int state = message.getState();
        ProtocolState[] values = ProtocolState.values();
        if (state < 0 || state >= values.length) {
            session.disconnect("State out of range");
            return;
        }

        ProtocolState newState = values[state];

        if (newState != ProtocolState.LOGIN && newState != ProtocolState.STATUS) {
            session.disconnect("Invalid state");
            return;
        }

        GlowServer.logger.info("Handshake [" + message.getAddress() + ":" + message.getPort() + "], next state " + newState);
        session.setState(newState);
    }
}
