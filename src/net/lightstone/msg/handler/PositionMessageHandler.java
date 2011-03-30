package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.model.Position;
import net.lightstone.msg.PositionMessage;
import net.lightstone.net.Session;

public final class PositionMessageHandler extends MessageHandler<PositionMessage> {

	@Override
	public void handle(Session session, Player player, PositionMessage message) {
		if (player == null)
			return;

		player.setPosition(new Position(message.getX(), message.getY(), message.getZ()));
	}

}
