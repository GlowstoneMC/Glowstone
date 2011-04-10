package net.glowstone.msg.handler;

import org.bukkit.Location;

import net.glowstone.entity.Player;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {

	@Override
	public void handle(Session session, Player player, PositionRotationMessage message) {
		if (player == null)
			return;

        // TODO: change 'null' to player.getWorld()
		player.setLocation(new Location(null, message.getX(), message.getY(), message.getZ(), message.getRotation(), message.getPitch()));
	}

}
