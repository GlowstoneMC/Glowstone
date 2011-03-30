package net.glowstone.msg.handler;

import net.glowstone.model.Player;
import net.glowstone.model.Position;
import net.glowstone.model.Rotation;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {

	@Override
	public void handle(Session session, Player player, PositionRotationMessage message) {
		if (player == null)
			return;

		player.setPosition(new Position(message.getX(), message.getY(), message.getZ()));
		player.setRotation(new Rotation(message.getRotation(), message.getPitch()));
	}

}
