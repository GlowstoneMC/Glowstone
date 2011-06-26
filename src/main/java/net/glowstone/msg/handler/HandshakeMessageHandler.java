package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.HandshakeMessage;
import net.glowstone.net.Session;
import net.glowstone.net.Session.State;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, HandshakeMessage message) {
        Session.State state = session.getState();
        if (state == Session.State.EXCHANGE_HANDSHAKE) {
            session.setState(State.EXCHANGE_IDENTIFICATION);
            session.send(new HandshakeMessage("-"));
        } else {
            session.disconnect("Handshake already exchanged.");
        }
    }

}
