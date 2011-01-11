package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.HandshakeMessage;
import net.lightstone.net.Session;
import net.lightstone.net.Session.State;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {

	@Override
	public void handle(Session session, Player player, HandshakeMessage message) {
		Session.State state = session.getState();
		if (state == Session.State.EXCHANGE_HANDSHAKE) {
			session.setState(State.EXCHANGE_IDENTIFICATION);
			session.send(new HandshakeMessage("-"));
		} else {
			session.disconnect("Handshake already exchanged.");
		}
	}

}
