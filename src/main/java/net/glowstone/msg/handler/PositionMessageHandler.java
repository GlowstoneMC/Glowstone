package net.glowstone.msg.handler;

import org.bukkit.Location;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.PositionMessage;
import net.glowstone.net.Session;

public final class PositionMessageHandler extends MessageHandler<PositionMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, PositionMessage message) {
		if (player == null)
			return;

        // TODO: change 'null' to player.getWorld()
		player.setLocation(new Location(null, message.getX(), message.getY(), message.getZ()));
	}

}
