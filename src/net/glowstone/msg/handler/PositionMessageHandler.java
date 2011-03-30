package net.glowstone.msg.handler;

import net.glowstone.model.Player;
import net.glowstone.model.Position;
import net.glowstone.msg.PositionMessage;
import net.glowstone.net.Session;

public final class PositionMessageHandler extends MessageHandler<PositionMessage> {

	@Override
	public void handle(Session session, Player player, PositionMessage message) {
		if (player == null)
			return;

		player.setPosition(new Position(message.getX(), message.getY(), message.getZ()));
	}

}
