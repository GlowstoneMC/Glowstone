package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.KickMessage;
import net.lightstone.net.Session;

public final class KickMessageHandler extends MessageHandler<KickMessage> {

	@Override
	public void handle(Session session, Player player, KickMessage message) {
		session.disconnect("Goodbye!");
	}

}
