package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.PingMessage;
import net.lightstone.net.Session;

public final class PingMessageHandler extends MessageHandler<PingMessage> {

	@Override
	public void handle(Session session, Player player, PingMessage message) {
		// TODO: reset timeout counter
	}

}
