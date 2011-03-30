package net.glowstone.msg.handler;

import net.glowstone.model.Player;
import net.glowstone.model.Rotation;
import net.glowstone.msg.RotationMessage;
import net.glowstone.net.Session;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {

	@Override
	public void handle(Session session, Player player, RotationMessage message) {
		if (player == null)
			return;

		player.setRotation(new Rotation(message.getRotation(), message.getPitch()));
	}

}
