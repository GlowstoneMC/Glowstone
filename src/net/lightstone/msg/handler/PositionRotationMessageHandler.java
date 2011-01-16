package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.model.Position;
import net.lightstone.model.Rotation;
import net.lightstone.msg.PositionRotationMessage;
import net.lightstone.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {

	@Override
	public void handle(Session session, Player player, PositionRotationMessage message) {
		if (player == null)
			return;

		player.setPosition(new Position(message.getX(), message.getY(), message.getZ()));
		player.setRotation(new Rotation(message.getRotation(), message.getPitch()));
	}

}
