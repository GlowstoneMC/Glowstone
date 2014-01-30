package net.glowstone.net.handler.handshake;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.StatusProtocol;

public class HandshakeHandler implements MessageHandler<GlowSession, HandshakeMessage> {

    @Override
    public void handle(GlowSession session, HandshakeMessage message) {
        int state = message.getState();
        Protocols[] values = Protocols.values();
        if (state < 0 || state >= values.length) {
            session.disconnect("State out of range");
            return;
        }

        Protocols newProtocol = values[state];

        GlowProtocol protocol;
        if (newProtocol == Protocols.LOGIN) {
            protocol = new LoginProtocol(session.getServer());
        } else if (newProtocol == Protocols.STATUS) {
            protocol = new StatusProtocol(session.getServer());
        } else {
            session.disconnect("Invalid state");
            return;
        }

        GlowServer.logger.info("Handshake [" + message.getAddress() + ":" + message.getPort() + "], next state " + newProtocol);
        session.setProtocol(protocol);

        if (newProtocol == Protocols.LOGIN) {
            if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated client!");
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated server!");
            }
        }
    }

    private static enum Protocols {
        HANDSHAKE,
        STATUS,
        LOGIN,
        PLAY
    }
}
