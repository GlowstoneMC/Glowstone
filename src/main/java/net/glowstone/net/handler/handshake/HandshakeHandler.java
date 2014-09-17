package net.glowstone.net.handler.handshake;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.ProtocolType;

public class HandshakeHandler implements MessageHandler<GlowSession, HandshakeMessage> {

    @Override
    public void handle(GlowSession session, HandshakeMessage message) {
        ProtocolType protocol = ProtocolType.getById(message.getState());
        if (protocol != ProtocolType.LOGIN && protocol != ProtocolType.STATUS) {
            session.disconnect("Invalid state");
            return;
        }

        session.setHostname(message.getAddress() + ":" + message.getPort());
        session.setProtocol(protocol);

        if (protocol == ProtocolType.LOGIN) {
            if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated client!");
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated server!");
            }
        }
    }
}
