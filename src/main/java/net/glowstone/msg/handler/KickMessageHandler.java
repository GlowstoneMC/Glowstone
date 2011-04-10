package net.glowstone.msg.handler;

import net.glowstone.entity.Player;
import net.glowstone.msg.KickMessage;
import net.glowstone.net.Session;

public final class KickMessageHandler extends MessageHandler<KickMessage> {

	@Override
	public void handle(Session session, Player player, KickMessage message) {
		session.disconnect("Goodbye!");
	}

}
