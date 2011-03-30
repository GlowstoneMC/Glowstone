package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.IdentificationMessage;
import net.lightstone.net.Session;
import net.lightstone.net.Session.State;

public final class IdentificationMessageHandler extends MessageHandler<IdentificationMessage> {

	@Override
	public void handle(Session session, Player player, IdentificationMessage message) {
		Session.State state = session.getState();
		if (state == Session.State.EXCHANGE_IDENTIFICATION) {
			session.setState(State.GAME);
			session.send(new IdentificationMessage(0, "", "", 0, 0));
			session.setPlayer(new Player(session, message.getName())); // TODO case-correct the name
		} else {
			boolean game = state == State.GAME;
			session.disconnect(game ? "Identification already exchanged." : "Handshake not yet exchanged.");
		}
	}

}
