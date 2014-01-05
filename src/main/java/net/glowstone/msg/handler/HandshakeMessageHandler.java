package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.HandshakeMessage;
import net.glowstone.net.ProtocolState;
import net.glowstone.net.Session;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, HandshakeMessage message) {
        ProtocolState state = session.getState();
        if (state == ProtocolState.HANDSHAKE) {
            session.setState(ProtocolState.LOGIN);
            if (session.getServer().getOnlineMode()) {
                session.send(new HandshakeMessage(session.getSessionId()));
            } else {
                session.send(new HandshakeMessage("-"));
            }
        } else {
            session.disconnect("Handshake already exchanged.");
        }
    }

}
