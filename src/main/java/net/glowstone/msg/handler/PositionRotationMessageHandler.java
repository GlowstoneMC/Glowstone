package net.glowstone.msg.handler;

import org.bukkit.Location;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, PositionRotationMessage message) {
		if (player == null)
			return;

        // TODO: change 'null' to player.getWorld()
		player.setRawLocation(new Location(null, message.getX(), message.getY(), message.getZ(), message.getRotation(), message.getPitch()));
	}

}
